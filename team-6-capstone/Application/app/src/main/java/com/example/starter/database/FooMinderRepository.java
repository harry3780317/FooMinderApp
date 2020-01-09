package com.example.starter.database;

import android.app.Application;
import android.os.AsyncTask;

import com.example.starter.database.dao.FridgeStatusDataDAO;
import com.example.starter.database.dao.ReciptDataDAO;
import com.example.starter.database.entity.FridgeStatusData;
import com.example.starter.database.entity.ReciptData;

import java.util.ArrayList;
import java.util.List;

public class FooMinderRepository {
    private ReciptDataDAO mReciptDataDAO;
    private FridgeStatusDataDAO mFridgeStatusDataDAO;
    private List<ReciptData> mReciptData;
    private List<FridgeStatusData> mFridgeStatusData;

    public FooMinderRepository(Application application) {
        FooMinderDatabase db = FooMinderDatabase.getDatabase(application);
        mReciptDataDAO = db.getReciptDataDAO();
        mFridgeStatusDataDAO = db.getFridgeStatusDAO();

        mReciptData = mReciptDataDAO.getReciptData();
        mFridgeStatusData = mFridgeStatusDataDAO.getFridgeStatusData();

    }

    public List<ReciptData> getReciptData() {
        return mReciptData;
    }
    public List<FridgeStatusData> getFridgeStatusData() {
        return mFridgeStatusData;
    }

    public ReciptData getReciptbyRow(int row) {
        return mReciptDataDAO.getReciptbyRow(row);
    }

    public void insert(ReciptData reciptData) {
        new insertReciptAsyncTask(mReciptDataDAO).execute(reciptData);
    }

    public void delete(ReciptData reciptData)
    {
        new deleteReciptAsyncTask(mReciptDataDAO).execute(reciptData);
    }

    public void deleteAll()
    {
        new deleteAllReciptAsyncTask(mReciptDataDAO).execute();
    }

    public void updateByColumnID(int newquantity, int id)
    {
        ReciptData params = new ReciptData(newquantity, id);
        new updateReciptAsyncTask(mReciptDataDAO).execute(params);
    }

    public void insert(FridgeStatusData fridgeStatusData) {
        new insertFridgeStatusAsyncTask(mFridgeStatusDataDAO).execute(fridgeStatusData);
    }


    private static class insertReciptAsyncTask extends AsyncTask<ReciptData, Void, Void> {

        private ReciptDataDAO mAsyncTaskDao;

        insertReciptAsyncTask(ReciptDataDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ReciptData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteReciptAsyncTask extends AsyncTask<ReciptData, Void, Void> {

        private ReciptDataDAO mAsyncTaskDao;

        deleteReciptAsyncTask(ReciptDataDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ReciptData... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class deleteAllReciptAsyncTask extends AsyncTask<ReciptData, Void, Void> {

        private ReciptDataDAO mAsyncTaskDao;

        deleteAllReciptAsyncTask(ReciptDataDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ReciptData... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class updateReciptAsyncTask extends AsyncTask<ReciptData, Void, Void> {

        private ReciptDataDAO mAsyncTaskDao;

        updateReciptAsyncTask(ReciptDataDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ReciptData... params) {
            mAsyncTaskDao.updateByColumnID(params[0].getQuantity(), params[0].getUid());
            return null;
        }
    }

    private static class insertFridgeStatusAsyncTask extends AsyncTask<FridgeStatusData, Void, Void> {

        private FridgeStatusDataDAO mAsyncTaskDao;

        insertFridgeStatusAsyncTask(FridgeStatusDataDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FridgeStatusData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
