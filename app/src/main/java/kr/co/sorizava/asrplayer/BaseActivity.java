package kr.co.sorizava.asrplayer;

import android.graphics.Color;
import android.os.Build;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mozilla.focus.R;

/**
 * BaseActivity
 */
public class BaseActivity extends AppCompatActivity {

    private  final  static  String TAG = "BaseActivity";


    /**
     * Custom ActionBar
     *
     * @param backTitle   뒤로가기 텍스트
     * @param title       title
     */
    public void initActionBar(String backTitle, String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar));

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(false);

            // 디자인 한대로 나오게 하기 위해서 레이아웃 파람을 설정
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            //actionbar_custom.xml 파일에 디자인 된 레이아웃 가져오기
            View actionbarView = getLayoutInflater().inflate(R.layout.custom_actionbar_title, null);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            actionBar.setCustomView(actionbarView, layout);

            //커스팀 액션바 레이아웃에 있는 버튼에 이벤트 걸때는 아래처럼 사용
            LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layout_back);
            layoutBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            /*
            ImageButton back_btn = (ImageButton) actionbarView.findViewById(R.id.backButtonImage);
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            */
            if (backTitle != null) {
                TextView backButtonTextView = (TextView) actionbarView
                    .findViewById(R.id.backButtonText);
                backButtonTextView.setText(backTitle);
            }

            if (title != null) {
                TextView titletv = (TextView) actionbarView.findViewById(R.id.title);
                titletv.setText(title);
            }
        }
    }

    public void initActionBar(String title, int resid) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar));

            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                actionBar.setHomeAsUpIndicator(resid);
            }
            actionBar.setTitle(null);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            TextView textView = new TextView(this);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_size));

            actionBar.setCustomView(textView, new ActionBar.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            actionBar.setDisplayShowCustomEnabled(true);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() ==  android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}

