package com.sggscc.myapplication_6;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
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

import android.widget.Button;
import android.widget.LinearLayout;
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
import java.nio.channels.FileChannel;
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

    private PlaceholderFragment frag;

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

                    frag.init_aqi(dataInPrint);
                    frag.init_sensor_values(dataInPrint);

               /*     if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
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
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
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
                        sensorView9.setText(" Sensor 9 Voltage = " + sensor_values[9] + "V");

                    }*/
                    recDataString.delete(0, recDataString.length()); 					//clear all string data
                    dataInPrint = " ";
                    // strIncom =" ";

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

    private String read_database() {


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
      //  int dataLength = recDataString1.length();
        // extract string
   //     txtString.setText("Data Received = " + recDataString1);
        						//get length of data received
    //    txtStringLength.setText("String Length = " + String.valueOf(dataLength));

/*
        String[] sensor_values = new String[10];
        for (int i = 0; i < sensor_values.length; i++)
        {
            sensor_values[i] = recDataString1.substring(0,recDataString1.indexOf(" ")-1);
            recDataString1 = recDataString1.substring(recDataString1.indexOf(" ")+1);
        }*/

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
        return recDataString1;
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
//frag.Init("778","#99cc00");
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

        @Override
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            String temp = "";
            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    if(readMessage != "~") {
                        database(readMessage);
                    }
                    else {
                        temp = readMessage;
                    }
                    // Send the obtained bytes to the UI Activity via handler
                    if(temp == "~") {
                        bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    }
                    // read_database();
                } catch (IOException e) {
                    break;
                }
            }
        }
        private void database(String data) {


            String sensor_value_1 = "", sensor_value_2 = "";
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
                sensor_value_1 = s;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (data.charAt(0) == '#')
            {
                String temp, temp1;
                temp = data.substring(1, data.indexOf(" "));
                for (int i = 0; i < 13; i++) {
                    if (i > 1 && i < 3) {
                        sensor_value_2 = temp;
                    }
                    temp1 = data.substring(data.indexOf(" ") + 1);
                    temp = temp1.substring(0, temp1.indexOf(" "));
                }
            }
            try {
                FileOutputStream fileout=openFileOutput("database.txt", MODE_APPEND);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                FileChannel channel = fileout.getChannel();
                if(channel.size() == 0)
                {
                    outputWriter.write(data);
                }
                else
                {
                    if (!contains(sensor_value_1,sensor_value_2)) {
                        outputWriter.write(data);
                    }

                }

                outputWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public boolean contains( String haystack, String needle ) {
            haystack = haystack == null ? "" : haystack;
            needle = needle == null ? "" : needle;

            // Works, but is not the best.
            //return haystack.toLowerCase().indexOf( needle.toLowerCase() ) > -1

            return haystack.toLowerCase().contains( needle.toLowerCase() );
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

    public void click1(View v)
    {
        frag.init_aqi("10 10 10 10 500 10 10 10 10 10 10 10 10");
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
       // MainActivity holder;
        private static final String ARG_SECTION_NUMBER = "section_number";
      //  private String DataString1;
        TextView pm25_value, pm10_value, co2_value, co_value, nh3_value, no2_value, o3_value, temperature, humidity;
        LinearLayout pm25, pm10, co2, co, nh3, no2, o3;

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
                temperature = (TextView) rootView.findViewById(R.id.temperature);
                humidity = (TextView) rootView.findViewById(R.id.humidity);
               // init_aqi();
              //  Init("778","#99cc00");
                return rootView;

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                View rootView = inflater.inflate(R.layout.fragment_sub_page02, container, false);

                pm25 = (LinearLayout) rootView.findViewById(R.id.pm25_layout);
                pm10 = (LinearLayout) rootView.findViewById(R.id.pm10_layout);
                co2 = (LinearLayout) rootView.findViewById(R.id.co2_layout);
                co = (LinearLayout) rootView.findViewById(R.id.co_layout);
                nh3 = (LinearLayout) rootView.findViewById(R.id.nh3_layout);
                no2 = (LinearLayout) rootView.findViewById(R.id.no2_layout);
                o3 = (LinearLayout) rootView.findViewById(R.id.o3_layout);

                pm25_value = (TextView) rootView.findViewById(R.id.pm25_value);
                pm10_value = (TextView) rootView.findViewById(R.id.pm10_value);
                co2_value = (TextView) rootView.findViewById(R.id.co2_value);
                co_value = (TextView) rootView.findViewById(R.id.co_value);
                nh3_value = (TextView) rootView.findViewById(R.id.nh3_value);
                no2_value = (TextView) rootView.findViewById(R.id.no2_value);
                o3_value = (TextView) rootView.findViewById(R.id.o3_value);
             //   pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
              //  init_sensor_values();

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

        public void Init(String value, String color)
        {
            int Value = Integer.parseInt(value);
            int per = (Value*100)/1024;
            waveProgressbar2.setCurrent(per, value);
            waveProgressbar2.setWaveColor(color);
            waveProgressbar2.setText("#FFFF00", 81);
        }

        public void init_sensor_values(String DataString1)
        {
            //DataString1 = holder.read_database();
            int j=0;
            String[] sensor_values = new String[7];
            for (int i = 0; i < 13; i++)
            {
                if(i > 5) {
                    sensor_values[j] = DataString1.substring(0, DataString1.indexOf(" "));
                    j++;
                }
                DataString1 = DataString1.substring(DataString1.indexOf(" ")+1);
            }

            pm25_value.setText(sensor_values[0]);

            int check = Integer.parseInt(sensor_values[0]);
            if (check >= 0 && check <= 30)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 31 && check <= 60)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 61 && check <= 90)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 91 && check <= 120)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 121 && check <= 250)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 250)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            pm10_value.setText(sensor_values[1]);

            check = Integer.parseInt(sensor_values[1]);
            if (check >= 0 && check <= 50)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 51 && check <= 100)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 101 && check <= 250)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 251 && check <= 350)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 351 && check <= 430)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 430)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            co2_value.setText(sensor_values[2]);

            check = Integer.parseInt(sensor_values[2]);
            if (check >= 0 && check <= 30)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 31 && check <= 60)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 61 && check <= 90)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 91 && check <= 120)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 121 && check <= 250)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 250)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            co_value.setText(sensor_values[3]);

            check = Integer.parseInt(sensor_values[3]);
            if (check >= 0 && check <= 1)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 1.1 && check <= 2)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 2.1 && check <= 10)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 11 && check <= 17)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 18 && check <= 34)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 34)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            nh3_value.setText(sensor_values[4]);

            check = Integer.parseInt(sensor_values[4]);
            if (check >= 0 && check <= 200)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 201 && check <= 400)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 401 && check <= 800)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 801 && check <= 1200)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 1201 && check <= 1800)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 1800)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            no2_value.setText(sensor_values[5]);

            check = Integer.parseInt(sensor_values[5]);
            if (check >= 0 && check <= 40)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 41 && check <= 80)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 81 && check <= 180)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 181 && check <= 280)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 281 && check <= 400)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 400)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));

            o3_value.setText(sensor_values[6]);

            check = Integer.parseInt(sensor_values[6]);
            if (check >= 0 && check <= 50)
                pm25.setBackgroundColor(Color.parseColor("#009933"));
            else if(check >= 51 && check <= 100)
                pm25.setBackgroundColor(Color.parseColor("#99cc00"));
            else if(check >= 101 && check <= 168)
                pm25.setBackgroundColor(Color.parseColor("#ffcc00"));
            else if(check >= 169 && check <= 208)
                pm25.setBackgroundColor(Color.parseColor("#ff9933"));
            else if(check >= 209 && check <= 748)
                pm25.setBackgroundColor(Color.parseColor("#ff0000"));
            else if(check > 748)
                pm25.setBackgroundColor(Color.parseColor("#ac3939"));
        }

        public void init_aqi(String DataString1)
        {
           // DataString1 = holder.read_database();
            int j = 0;
            String[] sensor_values = new String[3];
            for (int i = 0; i < 13; i++)
            {
                if(i > 3 && i < 7) {
                    sensor_values[j] = DataString1.substring(0, DataString1.indexOf(" "));
                    j++;
                }
                DataString1 = DataString1.substring(DataString1.indexOf(" ")+1);
            }

            temperature.setText(sensor_values[0]);
            humidity.setText(sensor_values[1]);
            int check = Integer.parseInt(sensor_values[2]);
            if (check >= 0 && check <= 50)
                Init(sensor_values[2],"#009933");
            else if(check >= 51 && check <= 100)
                Init(sensor_values[2],"#99cc00");
            else if(check >= 101 && check <= 200)
                Init(sensor_values[2],"#ffcc00");
            else if(check >= 201 && check <= 300)
                Init(sensor_values[2],"#ff9933");
            else if(check >= 301 && check <= 400)
                Init(sensor_values[2],"#ff0000");
            else if(check >= 401 && check <= 500)
                Init(sensor_values[2],"#ac3939");

        }


    }

}
