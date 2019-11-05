package org.techtown.businesscardapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;


public class nfcChangePage extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    ToggleButton tglReadWrite;
    TextView txtTagcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcchange);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tglReadWrite = (ToggleButton)findViewById(R.id.tglReadWrite);
        txtTagcontent = (TextView) findViewById(R.id.txtTagContent);
    }



    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8){
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8"):Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0:(17);
        char status = (char)(utfBit + langBytes.length);
        byte[] data =new byte[1+ langBytes.length + textBytes.length];
        data[0]=(byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data,1+ langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,new byte[0], data);
        return record;
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {


            if(tglReadWrite.isChecked())
            {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage)parcelables[0]);
                }else{
                    Toast.makeText(this,"ndef 메시지를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                }

            }else{
                NdefMessage ndefMessage = createNdefMessage(txtTagcontent.getText()+"");
                nfcAdapter.setNdefPushMessage(ndefMessage, this);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //NdefRecord ndefRecord = NdefRecord.createTextRecord("UTF-8","please...");
                //NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

                // /*여기에 내가 넣고싶은 메시지를 넣음 */

                //writeNdefMessage(tag,ndefMessage);
            }
        }

    }

    private void readTextFromMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[1];

            String tagcontent = getTextFromNdefRecord(ndefRecord);

            txtTagcontent.setText(tagcontent);

        }else{
            Toast.makeText(this,"ndef 레코드를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,nfcChangePage.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);

    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable ==null)
            {
                Toast.makeText(this,"태그가 ndef 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this,"태그가 쓰여졌습니다.2",Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Log.e("formatTag",e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
    {
        try {
            if(tag ==null)
            {
                Toast.makeText(this,"태그할 오브젝트가 널값이 아닙니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef==null)
            {
                //format tag with the ndef format and writes the message.
                formatTag(tag,ndefMessage);
            }
            else{
                ndef.connect();

                if(!ndef.isWritable())
                {
                    Toast.makeText(this,"태그에 쓸 수 없습니다.",Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this,"태그가 쓰여졌습니다.1",Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e)
        {
            Log.e("writeNdefMessage",e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content){
        try{
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = (content).getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        }catch (UnsupportedEncodingException e){
            Log.e("createTextRecord",e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord });

        return ndefMessage;
    }

    public  void tglReadWriteOnClick(View view){
        txtTagcontent.setText("");

    }

    public String getTextFromNdefRecord (NdefRecord ndefRecord)
    {
        String tagContent = null;

        try{
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, 0,
                    payload.length -5,textEncoding);
        }catch (UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecord",e.getMessage(),e);
        }
        return tagContent;
    }

}
