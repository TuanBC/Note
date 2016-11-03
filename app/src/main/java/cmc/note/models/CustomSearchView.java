package cmc.note.models;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;

/**
 * Created by tuanb on 02-Nov-16.
 */

public class CustomSearchView extends SearchView {
    public CustomSearchView(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            this.onActionViewCollapsed();
        }
        return super.dispatchKeyEventPreIme(event);
    }
}
