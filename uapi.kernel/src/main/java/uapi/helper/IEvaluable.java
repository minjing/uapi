package uapi.helper;

import java.util.Map;

/**
 * Created by xquan on 10/26/2016.
 */
public interface IEvaluable {

    boolean evalute(Map map);

    boolean evalute(IAttributed attributed);
}
