/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.bakumon.moneykeeper.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.bakumon.moneykeeper.database.entity.*
import java.util.*

/**
 * 记账记录表操作类
 *
 * @author Bakumon https://bakumon.me
 */
@Dao
interface RecordDao {

    @Query("SELECT Record.*, Assets.name as assetsName, RecordType.img_name as typeImgName, RecordType.name as typeName, RecordType.type from Record LEFT JOIN Assets on Record.assets_id=Assets.id LEFT JOIN RecordType on Record.record_type_id=RecordType.id ORDER BY time DESC, create_time DESC LIMIT :count")
    fun getRecordsForList(count: Int): LiveData<List<RecordForList>>

    @Query("SELECT Record.*, Assets.name as assetsName, RecordType.img_name as typeImgName, RecordType.name as typeName, RecordType.type from Record LEFT JOIN Assets on Record.assets_id=Assets.id LEFT JOIN RecordType on Record.record_type_id=RecordType.id WHERE Record.assets_id=:assetsId ORDER BY time DESC, create_time DESC LIMIT :limit")
    fun getRecordForListWithTypesByAssetsId(assetsId: Int, limit: Int): LiveData<List<RecordForList>>

    @Query("SELECT Record.*, Assets.name as assetsName, RecordType.img_name as typeImgName, RecordType.name as typeName, RecordType.type from Record LEFT JOIN Assets on Record.assets_id=Assets.id LEFT JOIN RecordType on Record.record_type_id=RecordType.id WHERE (RecordType.type=:type AND time BETWEEN :from AND :to) ORDER BY time DESC, create_time DESC")
    fun getRangeRecordForListWithTypes(from: Date, to: Date, type: Int): LiveData<List<RecordForList>>

    @Query("SELECT Record.*, Assets.name as assetsName, RecordType.img_name as typeImgName, RecordType.name as typeName, RecordType.type from Record LEFT JOIN Assets on Record.assets_id=Assets.id LEFT JOIN RecordType on Record.record_type_id=RecordType.id WHERE (RecordType.type=:type AND Record.record_type_id=:typeId AND time BETWEEN :from AND :to) ORDER BY time DESC, create_time DESC")
    fun getRangeRecordForListWithTypesByTypeId(from: Date, to: Date, type: Int, typeId: Int): LiveData<List<RecordForList>>

    @Query("SELECT Record.*, Assets.name as assetsName, RecordType.img_name as typeImgName, RecordType.name as typeName, RecordType.type from Record LEFT JOIN Assets on Record.assets_id=Assets.id LEFT JOIN RecordType on Record.record_type_id=RecordType.id WHERE (RecordType.type=:type AND Record.record_type_id=:typeId AND time BETWEEN :from AND :to) ORDER BY money DESC, create_time DESC")
    fun getRecordForListWithTypesSortMoney(from: Date, to: Date, type: Int, typeId: Int): LiveData<List<RecordForList>>

    @Insert
    fun insertRecord(record: Record)

    @Update
    fun updateRecords(vararg records: Record)

    @Delete
    fun deleteRecord(record: Record)

    @Query("SELECT RecordType.type AS type, sum(Record.money) AS sumMoney FROM Record LEFT JOIN RecordType ON Record.record_type_id=RecordType.id WHERE time BETWEEN :from AND :to GROUP BY RecordType.type")
    fun getSumMoney(from: Date, to: Date): List<SumMoneyBean>

    @Query("SELECT RecordType.type AS type, sum(Record.money) AS sumMoney FROM Record LEFT JOIN RecordType ON Record.record_type_id=RecordType.id WHERE time BETWEEN :from AND :to GROUP BY RecordType.type")
    fun getSumMoneyLiveData(from: Date, to: Date): LiveData<List<SumMoneyBean>>

    @Query("SELECT count(id) FROM Record WHERE record_type_id = :typeId")
    fun getRecordCountWithTypeId(typeId: Int): Long

    @Query("SELECT * FROM Record WHERE record_type_id = :typeId")
    fun getRecordsWithTypeId(typeId: Int): List<Record>?

    /**
     * 尽量使用 Flowable 返回，因为当数据库数据改变时，会自动回调
     * 而直接用 List ，在调用的地方自己写 Flowable 不会自动回调
     */
    @Query("SELECT RecordType.type AS type, Record.time AS time, sum(Record.money) AS daySumMoney FROM Record LEFT JOIN RecordType ON Record.record_type_id=RecordType.id where (RecordType.type=:type and Record.time BETWEEN :from AND :to) GROUP BY Record.time")
    fun getDaySumMoney(from: Date, to: Date, type: Int): LiveData<List<DaySumMoneyBean>>

    @Query("SELECT t_type.type AS type, t_type.img_name AS imgName,t_type.name AS typeName, Record.record_type_id AS typeId,sum(Record.money) AS typeSumMoney, count(Record.record_type_id) AS count FROM Record LEFT JOIN RecordType AS t_type ON Record.record_type_id=t_type.id where (t_type.type=:type and Record.time BETWEEN :from AND :to) GROUP by Record.record_type_id Order by sum(Record.money) DESC")
    fun getTypeSumMoney(from: Date, to: Date, type: Int): LiveData<List<TypeSumMoneyBean>>

    @Query("SELECT substr(datetime(substr(Record.time, 1, 10), 'unixepoch', 'localtime'), 1, 7) as month, RecordType.type AS type, sum(Record.money) AS sumMoney FROM Record LEFT JOIN RecordType ON Record.record_type_id=RecordType.id WHERE time BETWEEN :from AND :to GROUP BY RecordType.type, month ORDER BY Record.time DESC")
    fun getMonthOfYearSumMoney(from: Date, to: Date): LiveData<List<MonthSumMoneyBean>>

    @Query("SELECT RecordType.type AS type, Record.time AS time, sum(Record.money) AS daySumMoney FROM Record LEFT JOIN RecordType ON Record.record_type_id=RecordType.id where (RecordType.type=:type and Record.time BETWEEN :from AND :to) GROUP BY Record.time")
    fun getDaySumMoneyData(from: Date, to: Date, type: Int): List<DaySumMoneyBean>

}
