package com.cn.mcc.controller;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.talker.facade.Controller;
import com.baidu.aip.talker.facade.upload.LogBeforeUploadListener;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;
import com.cn.mcc.bean.Iat;
import com.cn.mcc.utils.BaseController;
import com.cn.mcc.utils.Constants;
import com.cn.mcc.utils.Result;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

//import org.json.JSONObject;

/**
 * Created by mervin on 2018/6/25.
 */
@RestController
public class BDVoiceController extends BaseController {
    public  final  static Logger logger=Logger.getLogger(BDVoiceController.class);
    //设置APPID/AK/SK
    public static final String APP_ID = "11572625";
    public static final String API_KEY = "t5fTe2YN25P36FYMnBTQcUga";
    public static final String SECRET_KEY = "6SgSI55vVqCXOHTamtYwG3yE7A9uKvGX";

    private String remoteHost="localhost";
    private int remotePort=8088;
    private DatagramSocket datagramSocket;

    public BDVoiceController() throws SocketException {
        datagramSocket = new DatagramSocket();
    }
    // 音频文件路径
    // private static final String AUDIO_PATH = "./resource/test_1.pcm";

    @RequestMapping(value="/voice/ttd",method = RequestMethod.POST)
    @ResponseBody
    public Result ttd(@RequestBody Iat iat) throws IOException,ParseException{
        String code="0";
        String msg="";
        String data="";
        try{
            System.out.println("请等待程序正常退出， 否则测试用户将导致10分钟内无法正常使用。");
            //  String dir = "src/test/resources/pcm";
            Controller controller = new Controller(new LogBeforeUploadListener(), new PrintAfterDownloadListener(), getProperties());

            BiccTest.asrOne(controller,iat.getFilePath());
            // BiccTest.asrBoth(controller, dir + "/salesman.pcm", dir + "/customer.pcm");

            InetAddress address = InetAddress.getByName(remoteHost);
            byte [] buffer = iat.getFilePath().getBytes();
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, address, remotePort);
            datagramSocket.send(packet);
            //接收数据报
            DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);
            datagramSocket.receive(inputPacket);
            System.out.println(new String(inputPacket.getData(), 0 , inputPacket.getLength()));

            controller.stop();

        }catch (Exception e){
            code= Constants.RESULT_MESSAGE_EXCEPTION;
            msg="转换失败";
        }
        return  result(code,msg,data);

    }

    @RequestMapping(value="/voice/vysb",method = RequestMethod.POST)
    @ResponseBody
    public Result vysb(@RequestBody Iat iat) throws IOException,ParseException{
        String code="0";
        String msg="";
        String data="";
        try{
            System.out.println("请等待程序正常退出， 否则测试用户将导致10分钟内无法正常使用。");
          //  String dir = "src/test/resources/pcm";
            Controller controller = new Controller(new LogBeforeUploadListener(), new PrintAfterDownloadListener(), getProperties());

            // 选择开启 asrOne 或者 asrBoth ， asrOne即一个通话1路音频， asrBoth为1个通话，2路音频
            // 8k_test.pcm 较短  salesman.pcm 较长，客服  customer.pcm 用户
            //  BiccTest.asrOne(controller, dir + "/8k_test.pcm");
            // BiccTest.asrBoth(controller, "src/test/resources/pcm/8k_test.pcm", "src/test/resources/pcm/8k_test.pcm");

            // 请等待程序正常退出，即end包发送完成。否则测试用户将导致10分钟内无法正常使用。
            BiccTest.asrOne(controller,iat.getFilePath());
           // BiccTest.asrOne(controller,dir + "/8k_test.pcm");
            // BiccTest.asrBoth(controller, dir + "/salesman.pcm", dir + "/customer.pcm");

            controller.stop();

        }catch (Exception e){
            code= Constants.RESULT_MESSAGE_EXCEPTION;
            msg="转换失败";
        }
        return  result(code,msg,data);

    }

    public static void main(String[] args) throws Exception {

        System.out.println("请等待程序正常退出， 否则测试用户将导致10分钟内无法正常使用。");
        String dir = "msc";
        Controller controller = new Controller(new LogBeforeUploadListener(), new PrintAfterDownloadListener(), getProperties());

        // 选择开启 asrOne 或者 asrBoth ， asrOne即一个通话1路音频， asrBoth为1个通话，2路音频
        // 8k_test.pcm 较短  salesman.pcm 较长，客服  customer.pcm 用户
        //  BiccTest.asrOne(controller, dir + "/8k_test.pcm");
        // BiccTest.asrBoth(controller, "src/test/resources/pcm/8k_test.pcm", "src/test/resources/pcm/8k_test.pcm");

        // 请等待程序正常退出，即end包发送完成。否则测试用户将导致10分钟内无法正常使用。
        BiccTest.asrOne(controller,dir + "/8k_test.pcm");
        // BiccTest.asrOne(controller,dir + "/customer.pcm");
        // BiccTest.asrBoth(controller, dir + "/salesman.pcm", dir + "/customer.pcm");
        controller.stop();
    }
    /**
     * 百度长语音转换
     * @param iat
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping(value="/voice/vtt",method = RequestMethod.POST)
    @ResponseBody
    public Result iat(@RequestBody Iat iat) throws IOException,ParseException{
        String code="0";
        String msg="";
        String data="";
        String analys="";
        try{
            AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
            // 对本地语音文件进行识别
           // String path = "c:\\temp\\hts002d4c04@ch348b0eb0ece0477600.wav";
            JSONObject asrRes = client.asr(iat.getFilePath(), "pcm", 16000, null);
            System.out.println(asrRes);

            // 对语音二进制数据进行识别
            //byte[] datas = Util.readFileByBytes(iat.getFilePath());     //readFileByBytes仅为获取二进制数据示例
            //org.json.JSONObject asrRes2 = client.asr(datas, "pcm", 16000, null);
         //  System.out.println(asrRes2);
           // JSONObject obj= JSON.parseObject(asrRes2);
           // System.out.println(asrRes2.getString("result"));
           //System.out.println(asrRes2.getString("err_msg"));
            if (asrRes.getString("err_msg").equals("success.")){
                data=asrRes.get("result").toString();
                code="200";
            }else{
                data=asrRes.get("result").toString();
            }

        }catch (Exception e){
            code= Constants.RESULT_MESSAGE_EXCEPTION;
            msg="转换失败";
        }
        return  result(code,msg,data);

    }
    /**
     * 默认读取conf/sdk.properties, 您也可以用下面的构造方法，传入Properties类
     *
     * Controller controller =
     * new Controller(new LogBeforeUploadListener(), new PrintAfterDownloadListener(), getProperties());
     * @return
     * @throws Exception
     */
    private static Properties getProperties() throws Exception {
        //String fullFilename = System.getProperty("user.dir") + "/conf/sdk.properties";
        String fullFilename = "src/main/resources/application.properties";
        Properties properties = new Properties();
        FileInputStream is = null;
        try {
            is = new FileInputStream(fullFilename);
            properties.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return properties;
    }
}
