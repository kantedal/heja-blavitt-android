package applications.kantedal.hejablvitt.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by filles-dator on 2015-07-10.
 */
public class HamsterText extends TextView {
    public HamsterText(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "g.otf"));
    }
}
