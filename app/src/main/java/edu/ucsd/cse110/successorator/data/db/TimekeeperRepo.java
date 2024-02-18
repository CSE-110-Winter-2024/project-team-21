package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.app.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Timekeeper;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class TimekeeperRepo implements Timekeeper {
    private final TimeDao timeDao;


    public TimekeeperRepo(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    @Override
    public Subject<LocalDateTime> getDateTime() {
        LiveData<TimeEntity> entityLiveData = timeDao.getTime();
        var timeLiveData = Transformations.map(entityLiveData, TimeEntity::toLocalTime);
        return new LiveDataSubjectAdapter<LocalDateTime>(timeLiveData);
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
        TimeEntity temp = new TimeEntity();
        temp.setLocalTime(dateTime.now().toString());
        timeDao.update(temp);
    }
}
