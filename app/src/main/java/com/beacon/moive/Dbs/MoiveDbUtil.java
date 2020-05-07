package com.beacon.moive.Dbs;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.beacon.moive.Beans.MoiveBean;
import com.beacon.moive.Beans.UserBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author Qumoy
 * Create Date 2020/2/3
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class MoiveDbUtil {
    private SqlHelper mSqlHelper;
    private Context mContext;

    public MoiveDbUtil(Context context) {
        mContext = context;
        //初始化数据库
        mSqlHelper = new SqlHelper(context, "Moive.db", null, 1);
    }

    /**
     * 写入Moive表
     */
    public void insertMoiveDb(int minor, String name, String pic, String actor, String time, String type, String description, List<String> postList) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        //将postlist转换为gson存入数据库
        Gson gson = new Gson();
        String post = gson.toJson(postList);
        ContentValues values = new ContentValues();
        values.put("minor", minor);
        values.put("name", name);
        values.put("pic", pic);
        values.put("actor", actor);
        values.put("time", time);
        values.put("type", type);
        values.put("description", description);
        values.put("post", post);
        long insert = db.insert("Moive", null, values);
        if (insert > 0) {
            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 跟新Moive表
     */
    public void updateMoiveDb(int minor, String name, String pic, String actor, String time, String type, String description, List<String> postList) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        Gson gson = new Gson();
        String post = gson.toJson(postList);
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("pic", pic);
        values.put("actor", actor);
        values.put("time", time);
        values.put("type", type);
        values.put("description", description);
        values.put("post", post);
        long insert = db.update("Moive", values, "minor=?", new String[]{String.valueOf(minor)});
        if (insert > 0) {
            Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 查询Moive表
     */
    public MoiveBean queryMoiveDb(int minor) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        //将json转换为List设置给MoiveBean
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        @SuppressLint("Recycle") Cursor cursor = db.query("Moive", null, "minor=" + minor, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MoiveBean moiveBean = new MoiveBean();
                moiveBean.setMinor(cursor.getInt(cursor.getColumnIndex("minor")));
                moiveBean.setMoivePic(cursor.getString(cursor.getColumnIndex("pic")));
                moiveBean.setMoiveName(cursor.getString(cursor.getColumnIndex("name")));
                moiveBean.setMoiveActor(cursor.getString(cursor.getColumnIndex("actor")));
                moiveBean.setMoiveTime(cursor.getString(cursor.getColumnIndex("time")));
                moiveBean.setMoiveType(cursor.getString(cursor.getColumnIndex("type")));
                moiveBean.setMoiveDescription(cursor.getString(cursor.getColumnIndex("description")));
                moiveBean.setMoivePost(gson.fromJson(cursor.getString(cursor.getColumnIndex("post")), type));
                return moiveBean;
            } while (cursor.moveToNext());
        }
        db.close();//数据库用完关闭
        return null;
    }

    /**
     * 遍历Moive表
     */
    public List<MoiveBean> queryMoiveDb() {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        List<MoiveBean> list = new ArrayList<>();
        //将json转换为List设置给MoiveBean
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        @SuppressLint("Recycle") Cursor cursor = db.query("Moive", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MoiveBean moiveBean = new MoiveBean();
                moiveBean.setMinor(cursor.getInt(cursor.getColumnIndex("minor")));
                moiveBean.setMoivePic(cursor.getString(cursor.getColumnIndex("pic")));
                moiveBean.setMoiveName(cursor.getString(cursor.getColumnIndex("name")));
                moiveBean.setMoiveActor(cursor.getString(cursor.getColumnIndex("actor")));
                moiveBean.setMoiveTime(cursor.getString(cursor.getColumnIndex("time")));
                moiveBean.setMoiveType(cursor.getString(cursor.getColumnIndex("type")));
                moiveBean.setMoiveDescription(cursor.getString(cursor.getColumnIndex("description")));
                moiveBean.setMoivePost(gson.fromJson(cursor.getString(cursor.getColumnIndex("post")), type));
                list.add(moiveBean);
            } while (cursor.moveToNext());
        }
        db.close();//数据库用完关闭
        return list;
    }

    /**
     * 删除Moive表中得数据
     */
    public void deleteMoiveDb(int minor) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        int delete = db.delete("Moive", "minor=?", new String[]{String.valueOf(minor)});
        if (delete > 0) {
            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }
/*************************************************************************************************************************/
    /**
     *关于User表的操作
     */

    /**
     * 写入User表
     */
    public void insertUserDb(String name, String password, String loginTime, String registerTime, int isUser) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("password", password);
        values.put("loginTime", loginTime);
        values.put("registerTime", registerTime);
        values.put("isUser", isUser);
        long insert = db.insert("User", null, values);
        if (insert > 0) {
            Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 跟新User表
     */
    public void updateUserDb(String name, String loginTime) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loginTime", loginTime);
        long insert = db.update("User", values, "name=?", new String[]{name});
        if (insert > 0) {
            Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
        }
        db.close();//数据库用完关闭
    }

    /**
     * 查询User表
     */
    public UserBean queryUserDb(String name) {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query("User", null, "name=" + "'" + name + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UserBean userBean = new UserBean();
                userBean.setName(cursor.getString(cursor.getColumnIndex("name")));
                userBean.setPasswod(cursor.getString(cursor.getColumnIndex("password")));
                userBean.setLoginTime(cursor.getString(cursor.getColumnIndex("loginTime")));
                userBean.setRegisterTime(cursor.getString(cursor.getColumnIndex("registerTime")));
                userBean.setIsUser(cursor.getInt(cursor.getColumnIndex("isUser")));
                return userBean;
            } while (cursor.moveToNext());
        }
        db.close();//数据库用完关闭
        return null;
    }

    /**
     * 遍历User表
     */
    public List<UserBean> queryUserDb() {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        List<UserBean> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.query("User", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UserBean userBean = new UserBean();
                userBean.setName(cursor.getString(cursor.getColumnIndex("name")));
                userBean.setPasswod(cursor.getString(cursor.getColumnIndex("password")));
                userBean.setLoginTime(cursor.getString(cursor.getColumnIndex("loginTime")));
                userBean.setRegisterTime(cursor.getString(cursor.getColumnIndex("registerTime")));
                userBean.setIsUser(cursor.getInt(cursor.getColumnIndex("isUser")));
                list.add(userBean);
            } while (cursor.moveToNext());
        }
        db.close();//数据库用完关闭
        return list;
    }
}
