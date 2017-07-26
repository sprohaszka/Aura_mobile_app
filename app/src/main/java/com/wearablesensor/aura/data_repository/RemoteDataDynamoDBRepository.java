/**
 * @file
 * @author  clecoued <clement.lecouedic@aura.healthcare>
 * @version 1.0
 *
 *
 * @section LICENSE
 *
 * Aura Mobile Application
 * Copyright (C) 2017 Aura Healthcare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 *
 * @section DESCRIPTION
 * RemeteDataDynamoDBRepository is a remote data storage relying on Amazon DynamoDB database and
 * Amazon Mobile SDK(API) <https://aws.amazon.com/mobile/sdk/>.
 * This framework saves data in a NoSql database with a multiple tables architecture
 *
 * We consider a three-step initialization:
 *  1) initialize database objects
 *  2) connect to DynamoDB database
 * Currently secured connection to database is done using Amazon Cognito User Pool credentials.
 *  3 .. N) query or save data in database
 */

package com.wearablesensor.aura.data_repository;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.wearablesensor.aura.data_repository.models.RRIntervalModel;
import com.wearablesensor.aura.data_repository.models.SeizureEventModel;
import com.wearablesensor.aura.user_session.UserModel;
import com.wearablesensor.aura.user_session.UserPreferencesModel;
import com.wearablesensor.aura.authentification.AmazonCognitoAuthentificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemoteDataDynamoDBRepository implements RemoteDataRepository.Session{
    private final String TAG = this.getClass().getSimpleName();

    private static final String DYNAMO_DB_IDENTITY_POOL_ID = "eu-west-1:8dbf4eef-78e6-4ac9-9ace-fa164cd83538"; /** Amazon dynamoDB identit/y pool */

    private Context mApplicationContext;
    private AmazonDynamoDBClient mAmazonDynamoDBClient;
    private DynamoDBMapper mDynamoDBMapper;

    /**
     * @brief constructor
     *
     * @param iApplicationContext Aura application context
     */

    public RemoteDataDynamoDBRepository(Context iApplicationContext){
        Log.d(TAG, "RemoteData DynamoDB repository init");
        mApplicationContext = iApplicationContext;
    }

    /**
     * @brief initialize connection between remote database and Aura application
     *
     * @param lAuthToken authentification token generated by identity provider
     * @throws Exception
     */

    public void connect(String lAuthToken) throws Exception{

        Log.d(TAG, "MyToken - " + lAuthToken);

        try {
            CognitoCachingCredentialsProvider lCredentialsProvider = new CognitoCachingCredentialsProvider(
                    mApplicationContext,    /* get the context for the application */
                    DYNAMO_DB_IDENTITY_POOL_ID,    /* Identity Pool ID */
                    Regions.EU_WEST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );

            Map<String, String> lLogins = new HashMap<String, String>();

            lLogins.put("cognito-idp.eu-west-1.amazonaws.com/" + AmazonCognitoAuthentificationHelper.userPoolId, lAuthToken);
            lCredentialsProvider.setLogins(lLogins);

            mAmazonDynamoDBClient = new AmazonDynamoDBClient(lCredentialsProvider);
            mAmazonDynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
            mDynamoDBMapper = new DynamoDBMapper(mAmazonDynamoDBClient);
        } catch (Exception e) {
            Log.d(TAG, "DynamoDB initialization fail" + e.getMessage());
            throw e;
        }
    }

    /**
     * @brief save a list of R-R interval samples
     *
     * @param iRrSamples list of R-R interval samples to be saved
     *
     * @throws Exception
     */

    public void saveRRSample(final ArrayList<RRIntervalModel> iRrSamples) throws Exception {
        Log.d(TAG, "save RR Samples: " + iRrSamples.size());

        try{
            mDynamoDBMapper.batchSave(iRrSamples);
            Log.d(TAG, "Success RR Samples DynamoDB");
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error RR Samples DynamoDB" + e.getMessage());
            throw e;
        }
    }
    /**
     * @brief save a list of seizure event samples
     *
     * @param iSensitiveEvents list of seizure event samples
     *
     * @throws Exception
     */


    public void saveSeizures(final ArrayList<SeizureEventModel> iSensitiveEvents) throws Exception {
        Log.d(TAG, "save Seizure event: " + iSensitiveEvents.size());

        try{
            mDynamoDBMapper.batchSave(iSensitiveEvents);
            Log.d(TAG, "Success save seizure event DynamoDB");
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error save seizure event DynamoDB" + e.getMessage());
            throw e;
        }
    }

    /**
     *
     * @brief query a user following authentification
     *
     * @param iAmazonId amazon cognito username
     *
     * @return
     * @throws Exception
     */
    @Override
    public UserModel queryUser(String iAmazonId) throws Exception{

        try{
            UserModel lUserModel = new UserModel();
            lUserModel.setAmazonId(iAmazonId);

            DynamoDBQueryExpression lQueryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(lUserModel)
                    .withConsistentRead(false);

            PaginatedQueryList<UserModel> lUserModelList = mDynamoDBMapper.query(UserModel.class, lQueryExpression);
            Log.d(TAG, "Query Fetched User Number: " + lUserModelList.size() + " " + iAmazonId );

            if(lUserModelList.size() == 1) {
                return lUserModelList.get(0);
            }
            else{
                throw new Exception();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error Fail GetUser" + e.getMessage());
            throw e;
        }
    }

    /**
     * @brief save a newly created user with information related to Authentification provider
     *
     * @param iUserModel user information related to Authentification
     *
     * @throws Exception
     */
    @Override
    public void saveUser(final UserModel iUserModel) throws Exception {
        try{
            mDynamoDBMapper.save(iUserModel);
            Log.d(TAG, "Success Save User");
        }
        catch(NotAuthorizedException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error Save User");
            throw e;
        }
    }

    /**
     * @brief query user preferences for a single user
     *
     * @param iUserId selected user UUID
     *
     * @return
     */
    @Override
    public UserPreferencesModel queryUserPreferences(String iUserId) {
        UserPreferencesModel lUserPrefsModel = null;
        try{
            lUserPrefsModel = mDynamoDBMapper.load(UserPreferencesModel.class, iUserId);
            Log.d(TAG, "Success GetUserPrefs");
            return lUserPrefsModel;
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error GetUserPrefs" + e.getMessage());
            throw e;
        }
    }

    /**
     * @brief save user preferences for a single user
     *
     * @param iUserPreferencesModel user preferences to be saved
     *
     * @throws Exception
     */
    @Override
    public void saveUserPreferences(final UserPreferencesModel iUserPreferencesModel) throws Exception {
        try{
            mDynamoDBMapper.save(iUserPreferencesModel);
            Log.d(TAG, "Success Save UserPreferences");
        }
        catch(Exception e){
            Log.d(TAG, "Error Save UserPreferences" + e.getMessage());
            throw e;
        }
    }
}
