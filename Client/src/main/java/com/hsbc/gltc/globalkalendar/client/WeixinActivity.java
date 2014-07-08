package com.hsbc.gltc.globalkalendar.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.hsbc.gltc.globalkalendar.util.Constants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle(R.string.weixinTitle);
        setContentView(R.layout.activity_weixin);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weixin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean sendWeixinMsg(View view) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_KEY, true);
        wxApi.registerApp(Constants.WEIXIN_APP_KEY);

        TextView wxMsgTV = (TextView) findViewById(R.id.weixinMsg);
        String wxMsg = wxMsgTV.getText().toString();

        WXTextObject textObject = new WXTextObject();
        textObject.text = wxMsg;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = wxMsg;

        RadioGroup sendTypeRG = (RadioGroup)findViewById(R.id.weixinSendTypeRG);
        int selectedRBId = sendTypeRG.getCheckedRadioButtonId();
        int scene;
        if (selectedRBId == R.id.sendToFriendsRB) {
            scene = SendMessageToWX.Req.WXSceneSession;
        } else if (selectedRBId == R.id.sendToTimelineRB) {
            scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            scene = SendMessageToWX.Req.WXSceneFavorite;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.scene = scene;
        req.message = msg;

        return wxApi.sendReq(req);
    }
}
