package com.hsbc.gltc.globalkalendar.client;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hsbc.gltc.globalkalendar.util.BitmapHelper;
import com.hsbc.gltc.globalkalendar.util.DBHelper;
import com.hsbc.gltc.globalkalendar.util.SysConstants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinActivity extends Activity {

    private String wxAppKey;
    private IWXAPI wxApi;
    private static final int THUMB_SIZE = 150;

    private ImageView imageView;
    private TextView imageSrc;
    private Button clearImgBtn;
    private TextView wxTitleTV;
    private TextView wxMsgTV;
    private TextView wxPicTV;
    private RadioGroup sendTypeRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin);

        wxAppKey = DBHelper.getSysProperties(this, SysConstants.WEIXIN_APP_KEY);
        wxApi = WXAPIFactory.createWXAPI(this, wxAppKey, true);
        wxApi.registerApp(wxAppKey);

        imageView = (ImageView) findViewById(R.id.weixinImage);
        imageSrc = (TextView) findViewById(R.id.weixinImageSrc);
        clearImgBtn = (Button) findViewById(R.id.weixinClearImgBtn);
        wxTitleTV = (TextView) findViewById(R.id.weixinTitle);
        wxMsgTV = (TextView) findViewById(R.id.weixinMsg);
        wxPicTV = (TextView) findViewById(R.id.weixinImageSrc);
        sendTypeRG = (RadioGroup)findViewById(R.id.weixinSendTypeRG);
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
        String title = wxTitleTV.getText().toString().trim();
        String text = wxMsgTV.getText().toString().trim();
        String imageSrc = wxPicTV.getText().toString().trim();

        if (text.length() == 0 && imageSrc.length() == 0) {
            Toast.makeText(this, R.string.weixinValidation, Toast.LENGTH_SHORT).show();
            return false;
        }

        WXMediaMessage msg;
        if (imageSrc.length() > 0){
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = "http://bt2043.github.io/WeDayDream";
            msg = new WXMediaMessage(webpageObject);

            //The image been selected, should set mediaObject as image
            WXImageObject imageObject = new WXImageObject();
            imageObject.imagePath = imageSrc;
            msg.mediaObject = imageObject;

            Bitmap bmp = BitmapFactory.decodeFile(imageSrc);
            Bitmap thumBmp = BitmapHelper.createScaledBMP(bmp, THUMB_SIZE);
            byte[] bytes = BitmapHelper.bmpToByteArray(thumBmp, true);
            bmp.recycle();
            msg.thumbData = bytes;
            bmp.recycle();
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
        req.transaction = "webpage" + System.currentTimeMillis();
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
        return false;
    }

    public void clearImage(View view) {
        imageView.setImageURI(null);
        clearImgBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);

            System.out.println(uri);
            imageSrc.setText(getMediaPathFromUri(uri));

            clearImgBtn.setVisibility(View.VISIBLE);
        }
    }

    private String getMediaPathFromUri(Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return uri.getPath();
        } else {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
//            boolean isExternalStorage = "com.android.externalstorage.documents".equals(uri.getAuthority());
//            //Uri mediaContentUri = isExternalStorage ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//            Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);

            String filePath = "";
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.onDestroy();
    }
}
