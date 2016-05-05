package red5pro.org.testandroidproject.tests.SubscribeTest;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class SubscribeTest extends TestDetailFragment {
    protected R5VideoView display;
    protected R5Stream subscribe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subscribe_test, container, false);

        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                                                    TestContent.GetPropertyString("host"),
                                                    TestContent.GetPropertyInt("port"),
                                                    TestContent.GetPropertyString("context"),
                                                    TestContent.GetPropertyFloat("buffer_time"));
        R5Connection connection = new R5Connection(config);

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
}
