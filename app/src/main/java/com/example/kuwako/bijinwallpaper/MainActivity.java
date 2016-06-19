package com.example.kuwako.bijinwallpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    PDKPin targetPin;
    List<PDKBoard> boardList;
    List<PDKPin> pinList;
    LinearLayout llPinList;
    Boolean loginFlg = false;
    Boolean logining = false;
    WallpaperManager wallpaperManager;
    // TODO 定数クラス
    final String PIN_COLUMNS = "id,link,url,board,media,image,attribution,metadata";
    final String BOARD_COLUMNS = "id,name,url,description,creator,image, counts";
    final String PDK_CLIENT_ID = "4819393203784402346";

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(MainActivity.this, "壁紙が変更されました。", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        llPinList = (LinearLayout) findViewById(R.id.llPinList);
        wallpaperManager = WallpaperManager.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("@@@fab_clicked", "clicked");
                if (loginFlg && pinList != null && pinList.size() > 0) {
                    Log.d("@@@fab_clicked", "targetPin is not null");
                    final ImageView imageView = new ImageView(view.getContext());
                    imageView.setClickable(true);
                    int index = (int) (Math.random() * pinList.size());
                    final PDKPin targetPin = pinList.get(index);

                    Picasso.with(getApplicationContext()).load(targetPin.getImageUrl()).into(imageView);
                    llPinList.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Picasso.with(getApplicationContext()).load(targetPin.getImageUrl()).into(target);
                        }
                    });
                } else if (logining == false) {
                    logining = true;
                    loginPinterest();
                }
            }
        });

        // pinterest系処理
        PDKClient.configureInstance(this, PDK_CLIENT_ID);
        PDKClient.getInstance().onConnect(this);
    }

    public void loginPinterest() {
        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

        PDKClient.getInstance().login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                getMyBoards();
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e("@@@login_failure" + getClass().getName(), exception.getDetailMessage());
            }
        });

    }

    public void getMyBoards() {
        PDKClient.getInstance().getMyBoards(BOARD_COLUMNS,
                new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        super.onSuccess(response);

                        boardList = response.getBoardList();
                        PDKBoard targetBoard = null;
                        for (int i = 0; i < boardList.size(); i++) {
                            PDKBoard board = boardList.get(i);

                            if (board.getName().equals("美人")) {
                                targetBoard = board;
                            }
                        }

                        String boardId = targetBoard.getUid();

                        if (boardId != null) {
                            getBoard(boardId);
                        }
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        super.onFailure(exception);
                        Log.e("@@@getMeFailed", exception.getDetailMessage());
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getBoard(String boardId) {
        Log.d("@@@getBoard", "XXXXXX");

        PDKClient.getInstance().getBoardPins(boardId, PIN_COLUMNS,
                new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        // TODO どうにかしてランダムにピンを取る方法を模索する
                        Log.e("@@@getBoardPinsBBB", "BBB");
                        pinList = response.getPinList();
                        loginFlg = true;
                        logining = false;
                        Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
                        Log.e("@@@getBoardBBB", String.valueOf(exception.getStausCode()));
                    }
                });
    }

}
