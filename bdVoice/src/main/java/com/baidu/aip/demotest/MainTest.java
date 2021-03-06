package com.baidu.aip.demotest;

import com.baidu.aip.talker.facade.Controller;
import com.baidu.aip.talker.facade.upload.LogBeforeUploadListener;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainTest {
    private static StringBuffer mResult = new StringBuffer();
    public static void main(String[] args) throws Exception {

        System.out.println("请等待程序正常退出， 否则测试用户将导致10分钟内无法正常使用。");
        String dir = "src/test/resources/pcm";
        Controller controller = new Controller(new LogBeforeUploadListener(), new PrintAfterDownloadListener());

        // 选择开启 asrOne 或者 asrBoth ， asrOne即一个通话1路音频， asrBoth为1个通话，2路音频
        // 8k_test.pcm 较短  salesman.pcm 较长，客服  customer.pcm 用户
        //  BiccTest.asrOne(controller, dir + "/8k_test.pcm");
        // BiccTest.asrBoth(controller, "src/test/resources/pcm/8k_test.pcm", "src/test/resources/pcm/8k_test.pcm");

        // 请等待程序正常退出，即end包发送完成。否则测试用户将导致10分钟内无法正常使用。
        BiccTest.asrOne(controller,dir + "/8k_test.pcm");
        // BiccTest.asrOne(controller,dir + "/customer.pcm");
        // BiccTest.asrBoth(controller, dir + "/salesman.pcm", dir + "/customer.pcm");
        System.out.println(mResult.toString());
        controller.stop();
    }

    public static String parseTxt(JsonNode node) throws IOException {
        //private boolean parseTxt(JsonNode node) throws IOException {
        String text="";
        if (node.has("roleCategory") && node.has("content")) {
            text = node.get("roleCategory").asText() + " ";
            if (node.has("extJson")) {
                JsonNode nodeExt = node.get("extJson");
                if (nodeExt.has("completed")) {
                    if (nodeExt.get("completed").asInt() == 1) {
                      //  text += "临时";
                    } else if (nodeExt.get("completed").asInt() == 3) {
                        text += "最终";
                        mResult.append("\n"+node.get("content").asText());
                    }
                }
            }
            text += "识别结果：" + node.get("content").asText();

            System.out.println(text);
            // return true;
        }
        // return false;
        return text;
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
        String fullFilename = System.getProperty("user.dir") + "/conf/sdk.properties";
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
