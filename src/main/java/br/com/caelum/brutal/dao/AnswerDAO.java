package br.com.caelum.brutal.dao;

import org.hibernate.Query;
import org.hibernate.Session;

import br.com.caelum.brutal.model.Answer;
import br.com.caelum.brutal.model.Question;
import br.com.caelum.brutal.model.User;
import br.com.caelum.brutal.model.VoteType;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class AnswerDAO implements VotableDAO {

	private final Session session;
    private final UserDAO userDao;
    private final QuestionDAO questionDAO;

	public AnswerDAO(Session session, QuestionDAO questionDAO, UserDAO userDao) {
		this.session = session;
        this.questionDAO = questionDAO;
        this.userDao = userDao;
	}
	
	public Answer getById(Long id) {
		return (Answer) session.load(Answer.class, id);
	}

	public void save(Answer answer) {
		this.session.save(answer);
	}

	public Answer create(String text, Question question, User author) {
		Question questionLoaded = questionDAO.getById(question.getId());
		User authorLoaded = userDao.findById(author.getId());
		
		questionLoaded.touchedBy(authorLoaded);
		
		Answer answer = new Answer(text, questionLoaded, authorLoaded);
		save(answer);
		return answer;
	}

	@Override
    public boolean alreadyVoted(Long answerId, User author, VoteType type) {
        Query query = session.createQuery("select v from Answer a " +
        		"join a.votes v where " +
        		"a.id=:answerId and v.author=:author and v.type=:type");
        query.setParameter("answerId", answerId);
        query.setParameter("author", author);
        query.setParameter("type", type);
        boolean voted = query.uniqueResult() != null;
        return voted;
    }
}




