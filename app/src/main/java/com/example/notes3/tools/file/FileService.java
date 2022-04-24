package com.example.notes3.tools.file;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.*;

/**
 * Created by flyan on 18-5-26.
 */

public class FileService {
    Context context;

    public FileService(Context context) {
        this.context = context;
    }

    /**
     * 保存一个文件到sdcard中
     *
     * @param fileName
     * @param data
     * @return
     */
    public boolean saveFileToSdcard(String fileName, byte[] data) {
        boolean flag = false;

        //先判断sdcard的可用状态
        String state = Environment.getExternalStorageState();
        FileOutputStream outputStream = null;
        //如果sdcard的状态为已挂载再进行下一步的操作
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File sdcardRoot = Environment.getExternalStorageDirectory();
            File newPath = new File(sdcardRoot.getAbsolutePath() + "/HelloNotes/"); //自定义一个文件夹

            if (!newPath.exists()) newPath.mkdirs(); //如果自定义的文件夹不存在，创建它

            File file = new File(newPath, fileName);
            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(data, 0, data.length);
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else Toast.makeText(context, "无sd卡，无法写入文件！！!", Toast.LENGTH_LONG).show();

        return flag;
    }

    /**
     * 读取一个文件从sdcard中
     *
     * @param fileName
     * @return
     */
    public String readContextFromSdcard(String fileName) {
        String result = null;
        byte[] data = new byte[1024];

        //先判断sdcard的可用状态
        String state = Environment.getExternalStorageState();
        FileInputStream inputStream = null;
        //如果sdcard的状态为已挂载再进行下一步的操作
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File sdcardRoot = Environment.getExternalStorageDirectory();
            File newPath = new File(sdcardRoot.getAbsolutePath() + "/Hello/");
            if (!newPath.exists()) newPath.mkdirs();
            File file = new File(newPath, fileName);
            if (file.exists()) {
                try {
                    inputStream = new FileInputStream(file);
                    inputStream.read(data, 0, 1024);
                    result = new String(data);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } else Toast.makeText(context, "无sd卡，无法写入文件！！!", Toast.LENGTH_LONG).show();

        return result;
    }

    /**
     * 通过存储文件的文件类型（通过后缀名判断）来放置到不同的公有文件夹
     *
     * @param fileName
     * @param data
     * @return
     */
    public boolean saveFileToSdcardBySuff(String fileName, byte[] data) {
        boolean flag = false;

        //先判断sdcard的可用状态
        String state = Environment.getExternalStorageState();
        File savaDir = null;
        File file = null;
        FileOutputStream outputStream = null;
        //如果sdcard的状态为已挂载再进行下一步的操作
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String suff = ""; //文件类型
            String[] temp = new String[]{};
            temp = fileName.split("\\."); //以.号为分隔符识别文件类型
            if (temp.length > 1) {
                suff = temp[1];
            } else {
                suff = "其他文件类型";
            }
            switch (suff) { //根据文件的类型放置到不同的公有文件夹中去
                case "mp3":
                    savaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    System.out.println("保存的文件类型为-->" + suff);
                    break;
                case "mp4":
                case "avi":
                case "3gp":
                    savaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    System.out.println("保存的文件类型为-->" + suff);
                    break;
                case "jpg":
                case "png":
                case "gif":
                    savaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    System.out.println("保存的文件类型为-->" + suff);
                    break;

                default:
                    savaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    System.out.println("保存的文件类型为-->" + suff);
                    break;
            }
            file = new File(savaDir, fileName);  //创建要保存的文件
            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(data, 0, data.length);
                flag = true;
            } catch (IOException ioE) {
                ioE.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return flag;
    }

    /**
     * 从sdcard上删除任意一个文件
     *
     * @param folderDir
     * @param fileName
     * @return
     */
    public boolean removeFileFromSdcard(String folderDir, String fileName){
        boolean flag = false;

        File file = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdcardRootDir = Environment.getExternalStorageDirectory();
            file = new File(sdcardRootDir.getAbsolutePath() + "/" + folderDir + "/" + fileName);
            if(file.exists()){
                file.delete();
                flag = true;
                Toast.makeText(context, "文件删除成功！", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(context, "该文件不存在，请检查文件路径！", Toast.LENGTH_LONG).show();
        }

        return flag;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

}
