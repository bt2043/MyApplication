package com.hsbc.gltc.globalkalendar.client;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hsbc.gltc.globalkalendar.util.DBHelper;
import com.hsbc.gltc.globalkalendar.util.SysConstants;
import com.sina.weibo.sdk.utils.BitmapHelper;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class WeixinActivity extends Activity {

    private String wxAppKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle(R.string.weixinTitle);
        setContentView(R.layout.activity_weixin);
        wxAppKey = DBHelper.getSysProperties(this, SysConstants.WEIXIN_APP_KEY);
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
        IWXAPI wxApi = WXAPIFactory.createWXAPI(this, wxAppKey, true);
        wxApi.registerApp(wxAppKey);

        TextView wxTitleTV = (TextView) findViewById(R.id.weixinTitle);
        TextView wxMsgTV = (TextView) findViewById(R.id.weixinMsg);
        TextView wxPicTV = (TextView) findViewById(R.id.weixinImageSrc);

        String title = wxTitleTV.getText().toString().trim();
        String text = wxMsgTV.getText().toString().trim();
        String imageSrc = wxPicTV.getText().toString().trim();

        if (text.length() == 0 || imageSrc.length() == 0) {
            Toast.makeText(this, R.string.weixinValidation, Toast.LENGTH_SHORT).show();
            return false;
        }

        System.out.println("title:\t" + title);
        System.out.println("text:\t" + text);
        System.out.println("imageSrc:\t" + imageSrc);

        WXMediaMessage msg;
        if (imageSrc.length() > 0){
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = DBHelper.getSysProperties(this, SysConstants.WEIBO_REDIRECT_URL);
            msg = new WXMediaMessage(webpageObject);
            //The image been selected, should set mediaObject as image
            WXImageObject imageObject = new WXImageObject();
            imageObject.imagePath = imageSrc;
            msg.mediaObject = imageObject;

            Bitmap bmp = BitmapFactory.decodeFile(imageSrc);
            msg.thumbData = bmpToByteArray(bmp, true);
        } else {
            msg = new WXMediaMessage();
            WXTextObject textObject = new WXTextObject();
            textObject.text = text;
            msg.mediaObject = textObject;
        }
        if (title.length() > 0) {
            msg.title = title;
        }
        msg.description = text;

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

       if (wxApi.sendReq(req)) {
           wxMsgTV.setText(R.string.blank);
           return true;
       } else {
           return false;
       }
    }

    public boolean selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
        //TODO
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            Uri uri = data.getData();
            ImageView imageView = (ImageView) findViewById(R.id.weixinImage);
            TextView imageSrc = (TextView) findViewById(R.id.weixinImageSrc);
            imageView.setImageURI(uri);
            imageSrc.setText(uri.getEncodedPath());
        }
    }


    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
