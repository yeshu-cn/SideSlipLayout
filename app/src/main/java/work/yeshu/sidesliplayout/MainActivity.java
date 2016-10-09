package work.yeshu.sidesliplayout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView btnDelete = (TextView) findViewById(R.id.btn_delete);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        final SideSlipLayout sideSlipLayout = (SideSlipLayout) findViewById(R.id.side_slip_layout);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Delete item", Snackbar.LENGTH_SHORT).show();
            }
        });

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Item content is clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

        sideSlipLayout.setOnMenuOpenListener(new SideSlipLayout.OnMenuOpenListener() {
            @Override
            public void onMenuOpen() {
                Log.i(TAG, "Menu is open");
                Snackbar.make(sideSlipLayout, "Item content is clicked", Snackbar.LENGTH_LONG).setAction("Close menu", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sideSlipLayout.isMenuOpen()) {
                            sideSlipLayout.closeMenu();
                        }
                    }
                }).show();
            }
        });
    }
}
