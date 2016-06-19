package applications.kantedal.hejablvitt.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by filles-dator on 2015-08-24.
 */

@ParseClassName("NewsSources")
public class NewsSource extends ParseObject{
    private boolean selected = true;

    public NewsSource(){
    }

    public String getName(){ return getString("name"); }
    public String getUrl(){ return getString("url"); }
    public boolean isSelected(){ return selected; }

    public void setSelected(boolean selected){ this.selected = selected; }
}
