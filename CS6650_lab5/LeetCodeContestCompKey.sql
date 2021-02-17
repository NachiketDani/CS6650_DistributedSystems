Create Schema if not exists LeetCodeContest;
USE LeetCodeContest; DROP TABLE IF EXISTS Records;
CREATE TABLE Records(Student VARCHAR(255) NOT NULL,ProblemID INT, PassOrFail BOOLEAN,SubmitTime TIME,Runtime INT,CONSTRAINT pk_Records_Compose PRIMARY KEY (Student, ProblemID));