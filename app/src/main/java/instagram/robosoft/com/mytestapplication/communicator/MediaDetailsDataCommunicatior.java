package instagram.robosoft.com.mytestapplication.communicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 26/2/16.
 */
public interface MediaDetailsDataCommunicatior {
    public void getMediaDetails(ArrayList<MediaDetails> l, Util util,Boolean flag);
}
