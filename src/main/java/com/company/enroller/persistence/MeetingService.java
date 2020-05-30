package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

    Session session;

    public MeetingService() {
        session = DatabaseConnector.getInstance().getSession();
    }

    public Collection<Meeting> getAll() {
        Criteria crit = session.createCriteria(Meeting.class);
        return crit.list();
    }

    public Meeting findById(long id) {
        Meeting meeting = (Meeting) this.session.get(Meeting.class, id);
//        if (meeting == null) {
//            throw new NoMeetingFoundException("No meeting with id '" + id + "' was found");
//        }
        return meeting;
    }

    public Collection<Meeting> findMeetings(String title, String description, Participant participant, String sortMode) {
        String hql = "FROM Meeting as meeting WHERE title LIKE :title AND description LIKE :description ";
        if (participant!=null) {
            hql += " AND :participant in elements(participants)";
        }
        if (sortMode.equals("title")) {
            hql += " ORDER BY title";
        }
        Query query = this.session.createQuery(hql);
        query.setParameter("title", "%" + title + "%").setParameter("description", "%" + description + "%");
        if (participant!=null) {
            query.setParameter("participant", participant);
        }

        return query.list();
    }

    public void delete(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.delete(meeting);
        transaction.commit();
    }

    public void add(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.save(meeting);
        transaction.commit();
    }

    public void update(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        this.session.merge(meeting);
        transaction.commit();
    }

    public boolean alreadyExist(Meeting meeting) {
        String hql = "FROM Meeting WHERE title=:title AND date=:date";
        Query query = this.session.createQuery(hql);
        Collection resultList = query.setParameter("title", meeting.getTitle()).setParameter("date", meeting.getDate())
                .list();
        return query.list().size() != 0;
    }


}
