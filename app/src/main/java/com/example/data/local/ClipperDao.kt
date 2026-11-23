package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipperDao {
    @Query("SELECT * FROM projects ORDER BY createdAtEpoch DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)

    @Query("SELECT * FROM clips ORDER BY indexOrder ASC")
    fun observeAllClips(): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips WHERE projectId = :projectId ORDER BY indexOrder ASC")
    fun observeClipsForProject(projectId: String): Flow<List<ClipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<ClipEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ClipEntity)

    @Query("DELETE FROM clips WHERE projectId = :projectId")
    suspend fun deleteClipsByProjectId(projectId: String)

    @Query("UPDATE clips SET captionStyleName = :captionStyle, captionColorHex = :colorHex, templateId = :templateId")
    suspend fun applyBrandKitToAllClips(captionStyle: String, colorHex: Long, templateId: String)

    @Query("SELECT * FROM scheduled_posts ORDER BY dayNumber ASC")
    fun observeScheduledPosts(): Flow<List<ScheduledPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledPosts(posts: List<ScheduledPostEntity>)

    @Query("DELETE FROM scheduled_posts")
    suspend fun clearScheduledPosts()
}
