package com.taha.todo.di

import android.app.Application
import androidx.room.Room
import com.taha.todo.data.TODODataBase
import com.taha.todo.data.TodoRepository
import com.taha.todo.data.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTodoDataBase(app: Application): TODODataBase {
        return Room.databaseBuilder(
            app,
            TODODataBase::class.java,
            "todo"
        ).build()
    }

    @Singleton
    @Provides
    fun providesTodoRepository(db: TODODataBase): TodoRepository {
        return TodoRepositoryImpl(db.dao)
    }
}