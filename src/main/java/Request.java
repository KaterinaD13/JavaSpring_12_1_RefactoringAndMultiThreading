import org.apache.commons.collections.map.MultiValueMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class Request {
    static final char QUERY_DELIMITER = '?';

    public static Map getQueryParams(String url) {
        Map parameter = new MultiValueMap();
        List<NameValuePair> params;
        try {
            params = URLEncodedUtils.parse(new URI(url), "UTF-8");
            for (NameValuePair param : params) {
                if (param.getName() != null && param.getValue() != null)
                    parameter.put(param.getName(), param.getValue());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return parameter;
    }

    public static String getQueryParamsPath(String url) {
        String result;
        int i = url.indexOf(QUERY_DELIMITER);
        if (i == -1) {
            return url;
        }
        result = url.substring(0, i);
        return result;
    }
}