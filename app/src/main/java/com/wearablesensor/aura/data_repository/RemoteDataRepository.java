/**
 * @file
 *
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
 *
 * RemoteDataRepository is an interface that describes the acces to remote storage
 * The remote storage implementation classes should be derivated from this interface
 *
 */

package com.wearablesensor.aura.data_repository;

import com.wearablesensor.aura.data_repository.models.PhysioSignalModel;
import com.wearablesensor.aura.data_repository.models.RRIntervalModel;
import com.wearablesensor.aura.data_repository.models.SeizureEventModel;
import com.wearablesensor.aura.user_session.UserModel;
import com.wearablesensor.aura.user_session.UserPreferencesModel;

import org.influxdb.InfluxDBFactory;

import java.util.ArrayList;

public interface RemoteDataRepository {

     interface Session {
        /**
         * @param lAuthToken authentification token generated by identity provider
         * @throws Exception
         * @brief initialize connection between remote database and Aura application
         */
        void connect(String lAuthToken) throws Exception;

        /**
         * @param iAmazonId amazon cognito username
         * @throws Exception
         * @brief query a user following authentification
         */
        UserModel queryUser(String iAmazonId) throws Exception;

        /**
         * @param iUserModel user information related to Authentification
         * @throws Exception
         * @brief save a newly created user with information related to Authentification provider
         */
        void saveUser(final UserModel iUserModel) throws Exception;

        /**
         * @param iUserId selected user UUID
         * @return user preferences
         * @throws Exception
         * @brief query user preferences for a single user
         */
        UserPreferencesModel queryUserPreferences(String iUserId) throws Exception;

        /**
         * @param iUserPreferencesModel user preferences to be saved
         * @throws Exception
         * @brief save user preferences for a single user
         */
        void saveUserPreferences(final UserPreferencesModel iUserPreferencesModel) throws Exception;

    }

    interface TimeSeries {
        /**
         * @brief initialize connection between remote database and Aura application
         *
         * @param iUser username credential
         * @param iPassword password credential
         *
         * @throws Exception
         */
        void connect(String iDatabaseURL, String iUser, String iPassword) throws Exception;

        /**
         * @param iPhysioSignalSamples list of physiological signal samples to be saved
         * @throws Exception
         * @brief save a list of physiological signal samples
         */
        void savePhysioSignalSamples(final ArrayList<PhysioSignalModel> iPhysioSignalSamples) throws Exception;

        /**
         * @param iSensitiveEvents list of seizure event samples
         * @throws Exception
         * @brief save a list of seizure event samples
         */
        void saveSeizures(final ArrayList<SeizureEventModel> iSensitiveEvents) throws Exception;
    }
}
