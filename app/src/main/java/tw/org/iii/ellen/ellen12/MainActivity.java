package tw.org.iii.ellen.ellen12;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private EditText input ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input) ;

        getMyIPV2() ;

        new Thread(){
            @Override
            public void run() {
                receiveUDP() ;
            }
        }.start();
    }

    private void getMyIP() {
        new Thread(){
            @Override
            public void run() {
                try {
                    String myip = InetAddress.getLocalHost().getHostAddress();
                    Log.v("ellen","myip = " + myip) ;
                }catch (Exception e){
                    Log.v("ellen",e.toString()) ;
                }
            }
        }.start();

    }

    private void getMyIPV2(){
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces() ;
            while (en.hasMoreElements()){
                NetworkInterface networkInterface = en.nextElement() ;
                Enumeration<InetAddress> ips = networkInterface.getInetAddresses() ;
                while (ips.hasMoreElements()){
                    InetAddress ip = ips.nextElement() ;
                    Log.v("ellen","ip = " + ip) ;
                }
            }


        }catch (Exception e){
            Log.v("ellen",e.toString()) ;
        }

    }


    public void sendUDP(View view) {
        new Thread(){
            @Override
            public void run() {
                byte[] data = input.getText().toString().getBytes() ;
                try{
                    DatagramSocket socket = new DatagramSocket() ;
                    DatagramPacket packet =
                            new DatagramPacket(
                                    data,data.length,InetAddress.getByName("10.0.103.69"),8888) ;
                    socket.send(packet) ;
                    socket.close() ;
                    Log.v("ellen","Send OK") ;
                }catch (Exception e){
                    Log.v("ellen",e.toString()) ;
                }
            }
        }.start();

    }

    private void receiveUDP(){
        while (true){
            byte[] buf = new byte[4096] ;
            try {
                System.out.println("Start.....");
                DatagramSocket socket = new DatagramSocket(8889) ;
                DatagramPacket packet = new DatagramPacket(buf, buf.length) ;
                socket.receive(packet) ;
                socket.close() ;

                String who = packet.getAddress().getHostAddress() ;
                byte[] data = packet.getData() ;
                int len = packet.getLength() ;
                String mesg = new String(data,0,len) ;
                Log.v("ellen",who +":"+ mesg) ;
                if (mesg.equals("quit"))
                    break ;


            }catch(Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
