# Twitter Follower Graph Analysis using MapReduce

## Project Overview
In this project, we implement a graph algorithm to process real Twitter data from 2010, specifically focusing on the follower graph, which represents relationships between users and their followers. The dataset contains user IDs and follower IDs, such as:

12,13 12,14 12,15 16,17


In the above example, users 13, 14, and 15 are followers of user 12, and user 17 is a follower of user 16.

The goal is to perform two Map-Reduce jobs:

1. **Job 1**: Count how many users each user follows.
2. **Job 2**: Group users by the number of users they follow and count how many users belong to each group.

The dataset contains 736,930 users and 36,743,448 links. A subset of this data is available as `small-twitter.csv` inside the project directory.

## Input Data
- **Complete dataset**: Available on Expanse at `/expanse/lustre/projects/uot195/fegaras/large-twitter.csv`.
- **Subset**: A smaller dataset (`small-twitter.csv`) is provided in the project directory for testing purposes.

## Map-Reduce Jobs

### Job 1: Count the Users Each User Follows
- **Mapper**:
    - Input: User ID, Follower ID (from CSV format `user_id,follower_id`).
    - Output: `(follower_id, user_id)`.
- **Reducer**:
    - Input: List of user IDs followed by a particular user (`follower_id`).
    - Output: `(follower_id, count)`, where `count` is the number of users followed by `follower_id`.

### Job 2: Group Users by the Number of Users They Follow
- **Mapper**:
    - Input: `follower_id` and `count` (output of Job 1).
    - Output: `(count, 1)`.
- **Reducer**:
    - Input: List of 1's for each `count`.
    - Output: `(count, sum)`, where `sum` is the number of users who follow exactly `count` users.

## Steps to Implement

1. **Job 1 Implementation**: 
   - Implement the first Map-Reduce job to count the number of users followed by each user.
   - Ensure the output is stored in the specified temporary directory.

2. **Job 2 Implementation**: 
   - Implement the second Map-Reduce job to group users by the number of users they follow and count how many users fall into each group.

3. **Final Output**: 
   - The final output will list counts of users who follow exactly `X` users. For example:
     ```
     10    30
     ```
     This means there are 30 users who follow exactly 10 users.

## Setup and Running the Code

### Prerequisites
- **Java**: Make sure you have Java installed on your machine.
- **Maven**: The project uses Maven for dependency management. Ensure Maven is installed.

### Running the Project on Expanse

1. **Build the Project**: 
   - Navigate to the project directory and run:
     ```bash
     mvn clean install
     ```

2. **Run the Map-Reduce Jobs**: 
   - After compiling, run the following command to execute the two Map-Reduce jobs on Expanse:
     ```bash
     ~/hadoop-3.2.2/bin/hadoop jar target/*.jar Twitter /path/to/small-twitter.csv tmp output
     ```
     Here:
     - `small-twitter.csv`: Path to the input file.
     - `tmp`: Temporary directory to store the output of the first job (input for the second job).
     - `output`: Final output directory.

### Optional: Running Locally using an IDE

If you prefer using an IDE (e.g., IntelliJ IDEA or Eclipse), follow these steps to set up:

#### IntelliJ IDEA
1. Go to **New → Project from Existing Sources** and choose your `project1` directory.
2. Select **Maven** as the project type.
3. Configure the Maven settings by going to **Run → Edit Configurations**.
4. Add a new configuration with the following parameters:
   - **Working directory**: `your project1 directory`.
   - **Command line**: `install`.
5. Build and run the project.

#### Eclipse
1. Import the project by going to **File → Import → Existing Maven Projects**.
2. Right-click on the project and select **Run As → Maven install** to compile the project.
3. Run the program as needed in the terminal.

## Notes
- **Input and Output Formats**: All input and output files should be in **text format**. Do not use binary formats.
- **Chaining Jobs**: The two Map-Reduce jobs should be chained together. Check the example on slide 13 of `bigdata-l03.pdf` for details on chaining jobs in Hadoop.

## Conclusion
This project provides hands-on experience with Map-Reduce algorithms in Hadoop, particularly in the context of processing large-scale graph data. By implementing these jobs, you will be able to efficiently analyze the Twitter follower graph and generate insightful reports.

---

## References
- [Hadoop Documentation](https://hadoop.apache.org/docs/)
- [GitHub: Twitter Follower Graph Analysis Project](https://github.com/your-repository)
