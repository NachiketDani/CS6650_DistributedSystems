Create Schema if not exists LeetCodeContest;
USE LeetCodeContest; DROP TABLE IF EXISTS Records;
CREATE TABLE Records(RecordID INT NOT NULL auto_increment,Student VARCHAR(255) NOT NULL,ProblemID INT, PassOrFail BOOLEAN,SubmitTime TIME,Runtime INT,CONSTRAINT pk_Records_RecordID PRIMARY KEY (RecordID));