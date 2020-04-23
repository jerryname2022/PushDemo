package com.push.demo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.umeng.message.PushAgent;

import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity {
    public static String TAG = "QOR_Car";


    public static String[] BLUETOOTH_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    TextView mTextView;
    String mBluetoothName = "QOR_Car";
    String mBluetoothMac;
    BluetoothClient mBluetoothClient;
    private UUID mService = UUID.fromString("6f410000-b5a3-f393-e0a9-e50e24dcca9e");
    private UUID mBlockingIdCharacter = UUID.fromString("6f410001-b5a3-f393-e0a9-e50e24dcca9e");
    private UUID mWriteCharacter = UUID.fromString("6f410003-b5a3-f393-e0a9-e50e24dcca9e");
    private UUID mReadCharacter = UUID.fromString("6f410005-b5a3-f393-e0a9-e50e24dcca9e");
    private UUID mNotifyCharacter = UUID.fromString("6f410004-b5a3-f393-e0a9-e50e24dcca9e");
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushAgent.getInstance(this).onAppStart();
        mTextView = findViewById(R.id.message_tv);
        mBluetoothClient = new BluetoothClient(getApplicationContext());


        mEditText = findViewById(R.id.action_et);

        findViewById(R.id.action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mUrl = mEditText.getText().toString();
                startWebActivity(mBaseUrl);
                //search();
            }
        });


        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        findViewById(R.id.notify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageNotify(mBluetoothMac);
            }
        });

        findViewById(R.id.connect_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        findViewById(R.id.auth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });

        boolean permissions = PermissionHelper.checkPermissions(this, BLUETOOTH_PERMISSIONS);
        if (!permissions) {
            PermissionHelper.requestPermissions(this, BLUETOOTH_PERMISSIONS, 101);
        }


        mBluetoothClient.registerBluetoothStateListener(new BluetoothStateListener() {

            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                Log.i(TAG, "onBluetoothStateChanged openOrClosed " + openOrClosed);
            }
        });
        mBluetoothClient.registerBluetoothBondListener(new BluetoothBondListener() {

            @Override
            public void onBondStateChanged(String mac, int bondState) {
                Log.i(TAG, "onBondStateChanged bondState " + bondState);
            }
        });

    }


    String mBaseUrl =
           // "https://np.beta.cu.stx.hk/ability/alive/alive.html?tradeId=611308179597002216&appid=LgKB9pF3a&timestamp=20200423104122133&token=064e7487be7d046dc0a31a80136fcba4";
            "http://pre-ocu4.venusplatform.com/living/person/index?type=vin&params=BMJb_GrWgC-zOTZBB_zsTnrosg-7QkcuxnWTB7NmeZU04AFUeBpvJz2IM0yY8sWtkqbK8v7k0gSqadscKXCop-kCbsqSptaZloe65QKabGX4mgE5qnUYv-Wo8evWYaIeBRJaDs6U";

    protected void startWebActivity(String url) {
        startActivity(new Intent(this, UnicomWebActivity.class).putExtra("URL", url));
    }


    public void search() {
        search(mBluetoothClient, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                mTextView.setText("searching ...");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Log.i(TAG, "onDeviceFounded name " + device.getName());

                if (TextUtils.equals(mBluetoothName, device.getName())) {
                    String text = mTextView.getText().toString();
                    mTextView.setText(text + "\r\n" + device.getName());

                    mBluetoothMac = device.getAddress();
                    stopSearch();
                    connect();
                }
            }

            @Override
            public void onSearchStopped() {
            }

            @Override
            public void onSearchCanceled() {
            }
        });
    }


    public void stopSearch() {
        mBluetoothClient.stopSearch();
    }

    public void search(BluetoothClient client, SearchResponse response) {
        // mViewWeakReference.get().show();
        if (client == null) return;
        SearchRequest request = new SearchRequest.Builder()
                //.searchBluetoothClassicDevice(20 * 1000, 1)
                .searchBluetoothLeDevice(20 * 1000, 1) //先扫BLE设备1次，每次20s
                .build();
        client.search(request, response);
    }


    public void connect() {
        connect(mBluetoothClient, mBluetoothMac, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                mTextView.setText("connect response code " + code);
                setBluetoothConfs(data);
                messageNotify(mBluetoothMac);
                Log.i(TAG, "connect response code " + code);
            }
        });
    }

    public void connect(BluetoothClient client, String mac, BleConnectResponse response) {
        // mViewWeakReference.get().show();
        if (client == null) return;

        BleConnectOptions options = new BleConnectOptions.Builder().setConnectRetry(5).build();
        client.connect(mac, options, response);
    }


    private void messageNotify(String mac) {
        if (mBluetoothClient.isBleSupported() && mBluetoothClient.isBluetoothOpened() && (!android.text.TextUtils.isEmpty(mac))) {
            messageNotify(mBluetoothClient, mac, mService, mNotifyCharacter, new BleNotifyResponse() {
                @Override
                public void onNotify(UUID service, UUID character, byte[] value) {

                    byte[] data = BluetoothUtils.parserData(value);
                    Log.i(TAG, "------------------------------------------------------------------");
                    Log.i(TAG, "notify data " + BluetoothUtils.format(value));
                    Log.i(TAG, "------------------------------------------------------------------");
//                    byte len = BluetoothUtils.parserLength(value);
//                    byte op1 = BluetoothUtils.parserOp1(value);
//                    byte op2 = BluetoothUtils.parserOp2(value);
//                    byte crc32 = BluetoothUtils.parserCrc32(value);

                    mTextView.setText("notify Response : " + BluetoothUtils.format(data));

                    if (mAuth) {
                        mAuth = false;
                        byte[] auth = BluetoothUtils.getAuthCmd(BluetoothUtils.parserData(value));
                        Log.i(TAG, "write " + BluetoothUtils.format(auth));
                        write(auth, true);
                    }

                }

                @Override
                public void onResponse(int code) {
                    mTextView.setText("notify response ...... code " + code);
                    Log.i(TAG, "onResponse notify code " + code);

                    auth();
                }
            });
        } else {
            mTextView.setText("蓝牙未打开......");
        }
    }

    public void messageNotify(BluetoothClient client, String mac, UUID service, UUID character, BleNotifyResponse response) {
        // mViewWeakReference.get().show();
        if (client == null) return;
        client.notify(mac, service, character, response);
    }

    boolean mAuth = false;

    public void auth() {
        String bookingId = "5421573F";
        // String secretKeyValue = "9f8f5421573f53c0";
        // String aesKey = "eb88275a3b444f97";

        mAuth = true;
        byte[] auth = BluetoothUtils.getAuthCmd(bookingId);
        Log.i(TAG, "auth write " + BluetoothUtils.format(bookingId.getBytes()));
        write(bookingId.getBytes(), true);
    }

    public void write(byte[] data, boolean block) {
        UUID writeUUID = block ? mBlockingIdCharacter : mWriteCharacter;

        write(mBluetoothClient, mBluetoothMac, mService, writeUUID, data, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                Log.i(TAG, "write Response code " + code + " " + (code == Code.REQUEST_SUCCESS));
            }
        });
    }

    public void write(BluetoothClient client, String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        // mViewWeakReference.get().show();
        if (client == null) return;
        client.write(mac, service, character, value, response);
    }


    protected void setBluetoothConfs(BleGattProfile bleGattProfile) {

        if (bleGattProfile == null) return;
        List<BleGattService> services = bleGattProfile.getServices();
        if (services != null && services.size() > 0) {
            BleGattService service = findCanReadAndWriteBleGattService(services);
            if (service != null) {
                mService = service.getUUID();
                List<BleGattCharacter> characters = service.getCharacters();
                for (BleGattCharacter character : characters) {

                    Log.i(TAG, "setBluetoothConfs Property " + character.getProperty() + " " + mService.toString());

                    if (character.getProperty() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                        mNotifyCharacter = character.getUuid();

                        Log.i(TAG, "setBluetoothConfs mNotifyCharacter " + mNotifyCharacter.toString() + " ");
                    } else if (character.getProperty() == BluetoothGattCharacteristic.PROPERTY_READ) {
                        mReadCharacter = character.getUuid();

                        Log.i(TAG, "setBluetoothConfs mReadCharacter " + mReadCharacter.toString() + " ");
                    } else if (character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE
                            || character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                            || character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE + BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
                        mWriteCharacter = character.getUuid();
                        Log.i(TAG, "setBluetoothConfs mWriteCharacter " + mWriteCharacter.toString() + " " + character.getProperty());

                    }
                }
            }
        }
    }


    protected BleGattService findCanReadAndWriteBleGattService(List<BleGattService> services) {
        if (services == null) return null;
        for (BleGattService service : services) {
            if (serviceCanReadAndWrite(service)) return service;
        }
        return null;
    }

    protected boolean serviceCanReadAndWrite(BleGattService service) {
        if (service == null) return false;
        List<BleGattCharacter> characters = service.getCharacters();

        boolean canWrite = false;
        boolean canRead = false;

        for (BleGattCharacter character : characters) {
            if (character.getProperty() == BluetoothGattCharacteristic.PROPERTY_NOTIFY || character.getProperty() == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
                canRead = true;
            } else if (character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE
                    || character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                    || character.getProperty() == BluetoothGattCharacteristic.PROPERTY_WRITE + BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
                canWrite = true;
            }
        }

        return canWrite && canRead;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
