package applications.kantedal.hejablvitt.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("_User")
public class User extends ParseUser{

    public ArrayList<String> getLikedNews(){
        return (ArrayList<String>)get("likedNews");
    }

    public ArrayList<String> getLikedComments() { return (ArrayList<String>) get("likedComments"); }

}
