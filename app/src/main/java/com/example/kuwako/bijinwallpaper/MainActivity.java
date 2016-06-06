package com.example.kuwako.bijinwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // pinterest系処理
        PDKClient.configureInstance(this, "4819393203784402346");
        PDKClient.getInstance().onConnect(this);

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

        PDKClient.getInstance().login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d("@@@login_success" + getClass().getName(), response.getData().toString());
                //user logged in, use response.getUser() to get PDKUser object
                Log.d("@@@userImageUrl", response.getUser().getImageUrl());

                PDKClient.getInstance().getPath("me/", null, new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.d("@@@username", response.getUser().getFirstName());
                        PDKUser user = response.getUser();
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.d("@@@username", "getPathFailed");
                    }
                });


                PDKClient.getInstance().getPath("boards/mrmasakik/美人/", null, new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response){
                        Log.d("@@@board", response.getBoard().getDescription());
                        List<PDKPin> pinList = response.getPinList();
                        PDKPin pinTemp = response.getPin();

                        Log.d("@@@pinTemp", pinTemp.getImageUrl());
                        for (int i = 0; i < pinList.size() ;i++) {
                            PDKPin pin = pinList.get(i);
                            if (i == 1) {
                                Log.d("@@@pinUrl", pin.getImageUrl());
                                Log.d("@@@pinLink", pin.getLink());
                                Log.d("@@@pinMetaData", pin.getMetadata());
                                Log.d("@@@pinNote", pin.getNote());
                                Log.d("@@@pinUid", pin.getUid());
                            }
                        }
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.d("@@@borad", exception.getMessage() + " : " + exception.getDetailMessage() + " : " + exception.getLocalizedMessage());
                    }
                });


            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e("@@@login_failure" + getClass().getName(), exception.getDetailMessage());
            }
        });

//        getPinList();

//        PDKClient.getInstance().getMe("id,image,counts,created_at,first_name,last_name,bio,username",
//                new PDKCallback() {
//                    @Override
//                    public void onSuccess(PDKResponse response) {
//                        super.onSuccess(response);
//
//                        PDKUser user = response.getUser();
//                        Log.e("@@@getMe", user.getImageUrl());
//                    }
//
//                    @Override
//                    public void onFailure(PDKException exception) {
//                        super.onFailure(exception);
//                        Log.e("@@@getMeFailed", exception.getDetailMessage());
//                    }
//
//                }
//        );

        PDKClient.getInstance().getMyBoards("id,name,url,description,creator,image",
                new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        super.onSuccess(response);

                        List<PDKBoard> boardList = response.getBoardList();
                        PDKBoard targetBoard = null;
                        for (int i = 0; i < boardList.size(); i++) {
                            PDKBoard board = boardList.get(i);
                            Log.e("@@@getBoards", boardList.get(i).getName());
                            Log.e("@@@getBoards", boardList.get(i).getImageUrl());

                            if (board.getName().equals("美人")) {
                                targetBoard = board;
                            }
                        }

                        Log.e("@@@targetBoard", targetBoard.getName());
                        Log.e("@@@targetBoard", String.valueOf(targetBoard.getPinsCount()));
                        Log.e("@@@targetBoard", targetBoard.getUid());
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        super.onFailure(exception);
                        Log.e("@@@getMeFailed", exception.getDetailMessage());
                    }

                }
        );

        Log.e("@@@aaa", "aaa");
        PDKClient.getInstance().getMyPins("id,link,url,creator,board,media,image,attribution,metadata",
                new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        super.onSuccess(response);

                        Log.e("@@@getPin", "onSuccess");
                        List<PDKPin> pinList = response.getPinList();

                        Boolean isBijin = false;
                        PDKPin bijinPin = null;

                        while (!isBijin) {
                            int randomNum = (int) (Math.random() * pinList.size());
                            bijinPin = pinList.get(randomNum);

                            if (bijinPin == null) {
                                Log.e("@@@bijinPin", "bijinPin is null");
                                return;
                            }

//                            if (bijinPin.getBoard() != null && bijinPin.getBoard().getName().equals("美人")) {
                                isBijin = true;
//                            }
                        }

                        Log.e("@@@getPin", bijinPin.getImageUrl());
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        super.onFailure(exception);
                        Log.e("@@@getMyPinsFailed", exception.getDetailMessage());
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

    public void getPinterestUser() {
        Log.d("@@@getPinterestUser", "called");
    }

    public void getPinList() {
        Log.d("@@@getPinList", "called");
    }
}
