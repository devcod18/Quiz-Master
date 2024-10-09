    package com.example.quizmaster.repository;

    import com.example.quizmaster.entity.Question;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.List;

    public interface QuestionRepository extends JpaRepository<Question, Long> {
        List<Question> findAllByQuizId(Long quizId);
        List<Question> findAllByQuizIdIn(List<Long> quizIds);

        @Query(value = "SELECT * FROM questions WHERE quiz_id = :quizId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
        List<Question> findRandomQuestionsByQuizId(@Param("quizId") Long quizId, @Param("limit") int limit);
    }
