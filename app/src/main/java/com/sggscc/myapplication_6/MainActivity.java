package com.sggscc.myapplication_6;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
  
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.fanrunqi.waveprogress.*;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    Handler bluetoothIn;
    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private String recDataString1;


    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    //   read_database();
                    //if message is what we want
                    String readMessage = (String) msg.obj;                       // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    //keep appending to string until ~
                    // int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    //if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(1);    // extract string
             //       txtString.setText("Data Received = " + dataInPrint);
                    int dataLength = dataInPrint.length();							//get length of data received
             //       txtStringLength.setText("String Length = " + String.valueOf(dataLength));


                    if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                    {
                        String temp, temp1;
                        temp = recDataString.substring(1,recDataString.indexOf(" "));
                           /* String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");	//update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");*/
                        String[] sensor_values = new String[10];
                        for (int i = 0; i < sensor_values.length; i++)
                        {
                            sensor_values[i] = temp;
                            temp1 = recDataString.substring(recDataString.indexOf(" ")+1);
                            temp = temp1.substring(0,temp1.indexOf(" "));
                        }

               /*         sensorView0.setText(" Sensor 0 Voltage = " + sensor_values[0] + "V");	//update the textviews with sensor values
                        sensorView1.setText(" Sensor 1 Voltage = " + sensor_values[1] + "V");
                        sensorView2.setText(" Sensor 2 Voltage = " + sensor_values[2] + "V");
                        sensorView3.setText(" Sensor 3 Voltage = " + sensor_values[3] + "V");
                        sensorView4.setText(" Sensor 4 Voltage = " + sensor_values[4] + "V");
                        sensorView5.setText(" Sensor 5 Voltage = " + sensor_values[5] + "V");
                        sensorView6.setText(" Sensor 6 Voltage = " + sensor_values[6] + "V");
                        sensorView7.setText(" Sensor 7 Voltage = " + sensor_values[7] + "V");
                        sensorView8.setText(" Sensor 8 Voltage = " + sensor_values[8] + "V");
                        sensorView9.setText(" Sensor 9 Voltage = " + sensor_values[9] + "V");*/

                    }
                    recDataString.delete(0, recDataString.length()); 					//clear all string data
                    // strIncom =" ";
                    dataInPrint = " ";
                    //}
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
   /*     btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
       ;
    }

    private void read_database() {

        String response =  "";

        try {
            FileInputStream fis = openFileInput("database.txt");
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer= new char[100];
            String s="";
            int charRead;

            while ((charRead=isr.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            isr.close();
            recDataString1 = s.substring(s.lastIndexOf("#")+1);


        } catch (Exception e) {
            e.printStackTrace();
        }
        int dataLength = recDataString1.length();
        // extract string
   //     txtString.setText("Data Received = " + recDataString1);
        						//get length of data received
    //    txtStringLength.setText("String Length = " + String.valueOf(dataLength));


        String[] sensor_values = new String[10];
        for (int i = 0; i < sensor_values.length; i++)
        {
            sensor_values[i] = recDataString1.substring(0,recDataString1.indexOf(" ")-1);
            recDataString1 = recDataString1.substring(recDataString1.indexOf(" ")+1);
        }

   /*     sensorView0.setText(" Sensor 0 Voltage = " + sensor_values[0] + "V");	//update the textviews with sensor values
        sensorView1.setText(" Sensor 1 Voltage = " + sensor_values[1] + "V");
        sensorView2.setText(" Sensor 2 Voltage = " + sensor_values[2] + "V");
        sensorView3.setText(" Sensor 3 Voltage = " + sensor_values[3] + "V");
        sensorView4.setText(" Sensor 4 Voltage = " + sensor_values[4] + "V");
        sensorView5.setText(" Sensor 5 Voltage = " + sensor_values[5] + "V");
        sensorView6.setText(" Sensor 6 Voltage = " + sensor_values[6] + "V");
        sensorView7.setText(" Sensor 7 Voltage = " + sensor_values[7] + "V");
        sensorView8.setText(" Sensor 8 Voltage = " + sensor_values[8] + "V");
        sensorView9.setText(" Sensor 9 Voltage = " + sensor_values[9] + "V");*/

        recDataString1 = "";
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
      //  mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    database(readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    // read_database();
                } catch (IOException e) {
                    break;
                }
            }
        }
        private void database(String data) {

            try {
                FileOutputStream fileout=openFileOutput("database.txt", MODE_APPEND);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                outputWriter.write(data);
                outputWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }


    }

    public void click(View v) {
        Intent intent;

        intent = new Intent(this,First.class);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    
  

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "AQI";
                case 1:
                    return "Gases";
                case 2:
                    return "About";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        @InjectView(R.id.aqi)
        cn.fanrunqi.waveprogress.WaveProgressView waveProgressbar2;
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {

                View rootView = inflater.inflate(R.layout.fragment_sub_page01, container, false);
                ButterKnife.inject(this,rootView);
                Init();
                return rootView;

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                View rootView = inflater.inflate(R.layout.fragment_sub_page02, container, false);
                return rootView;
            }

            else
            {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
        }
        public void Init() {
            waveProgressbar2.setCurrent(77, "788M/1024M");
            waveProgressbar2.setWaveColor("#5b9ef4");
            waveProgressbar2.setText("#FFFF00", 41);
        }
        public void click(View v) {
            Intent intent;

            intent = new Intent(getActivity(),First.class);

            startActivity(intent);
        }


    }

}
