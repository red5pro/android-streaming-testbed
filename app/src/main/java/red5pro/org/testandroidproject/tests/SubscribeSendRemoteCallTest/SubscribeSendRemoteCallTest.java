package red5pro.org.testandroidproject.tests.SubscribeSendRemoteCallTest;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.event.R5RemoteCallContainer;
import com.red5pro.streaming.view.R5VideoView;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 4/26/16.
 */
public class SubscribeSendRemoteCallTest extends TestDetailFragment {
    private TextView messageView;
    protected R5VideoView display;
    protected R5Stream subscribe;
    private R5Connection connection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subscribe_send_test, container, false);
        Button clickButton = (Button) view.findViewById(R.id.send_button);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(connection!=null ){
                            Map<Object,Object> params = new HashMap<Object,Object>();
                            params.put("hello","world");
                            R5RemoteCallContainer rpc = new R5RemoteCallContainer("sendMessageToPublisher",params);
                            connection.call(rpc);
                        }
                    }
                }).start();

            }
        });
        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("buffer_time"));
        connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //find the view and attach the stream
        display = (R5VideoView) view.findViewById(R.id.videoView);
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyBool("debug_view"));

        subscribe.play(TestContent.GetPropertyString("stream1"));

        return view;
    }

    @Override
    public void onStop() {
        if(subscribe != null) {
            subscribe.stop();
        }

        super.onStop();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribe.client = this;
    }

    public void whateverFunctionName( String message ){

        System.out.println("Recieved message from publisher: " + message);

        String[] parsedMessage = message.split(";");
        Hashtable<String, String> map = new Hashtable<String, String>();
        for (String s : parsedMessage) {
            String key = s.split("=")[0];
            String value = s.split("=")[1];
            System.out.println("Received key: " + key + "; with value: " + value);

            map.put(key,value);
        }

        final Hashtable<String, String> mapFinal = map;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (messageView == null) {
                    messageView = new TextView(display.getContext());
                    ((FrameLayout) display.getParent()).addView(messageView);
                    messageView.setBackgroundColor(Color.LTGRAY);
                }

                if (mapFinal.containsKey("message")) {
                    messageView.setText(mapFinal.get("message"));
                }

                FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (mapFinal.containsKey("touchX")) {
                    position.leftMargin = (int) (Float.parseFloat(mapFinal.get("touchX")) * display.getWidth());
                }
                if (mapFinal.containsKey("touchY")) {
                    position.topMargin = (int) (Float.parseFloat(mapFinal.get("touchY")) * display.getHeight());
                }
                messageView.setLayoutParams(position);
            }
        });
    }
}
