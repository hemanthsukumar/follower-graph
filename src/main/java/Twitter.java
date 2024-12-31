import java.io.*;
import java.util.Scanner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Twitter {

    // Mapper for Job 1: Emits <follower_id, user_id> pairs
    public static class FollowerPairMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        private IntWritable followerId = new IntWritable();
        private IntWritable userId = new IntWritable();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Split line by comma and extract user_id and follower_id
            String[] ids = value.toString().split(",");
            if (ids.length == 2) {
                // Parse the user and follower IDs and emit <followerId, userId>
                userId.set(Integer.parseInt(ids[0]));
                followerId.set(Integer.parseInt(ids[1]));
                context.write(followerId, userId);
            }
        }
    }

    // Reducer for Job 1: Count how many users each follower is following
    public static class FollowerCountReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        private IntWritable totalFollowers = new IntWritable();

        @Override
        public void reduce(IntWritable followerId, Iterable<IntWritable> userIds, Context context)
                throws IOException, InterruptedException {
            int followCount = 0;
            // Count the number of users followed by each followerId
            for (IntWritable ignored : userIds) {
                followCount++;
            }
            totalFollowers.set(followCount);
            context.write(followerId, totalFollowers);
        }
    }

    // Mapper for Job 2: Emit <count, 1> for each user's follower count
    public static class FollowerCountMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        private IntWritable followCount = new IntWritable();
        private final IntWritable singleCount = new IntWritable(1);

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Split line by tab and extract follower count
            String[] tokens = value.toString().split("\t");
            if (tokens.length == 2) {
                followCount.set(Integer.parseInt(tokens[1]));
                context.write(followCount, singleCount);
            }
        }
    }

    // Reducer for Job 2: Sum up the number of users with the same follower count
    public static class UserCountReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        private IntWritable totalUsers = new IntWritable();

        @Override
        public void reduce(IntWritable count, Iterable<IntWritable> counts, Context context)
                throws IOException, InterruptedException {
            int userSum = 0;
            // Sum the number of users with the same follow count
            for (IntWritable val : counts) {
                userSum += val.get();
            }
            totalUsers.set(userSum);
            context.write(count, totalUsers);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        // Job 1: Calculate how many users each follower follows
        Job firstJob = Job.getInstance(conf, "Calculate Follower Counts");
        firstJob.setJarByClass(Twitter.class);
        firstJob.setMapperClass(FollowerPairMapper.class);
        firstJob.setReducerClass(FollowerCountReducer.class);
        firstJob.setOutputKeyClass(IntWritable.class);
        firstJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(firstJob, new Path(args[0]));
        FileOutputFormat.setOutputPath(firstJob, new Path(args[1]));
        if (!firstJob.waitForCompletion(true)) {
            System.exit(1);
        }

        // Job 2: Group users by the number of people they follow
        Job secondJob = Job.getInstance(conf, "Group Users by Follower Count");
        secondJob.setJarByClass(Twitter.class);
        secondJob.setMapperClass(FollowerCountMapper.class);
        secondJob.setReducerClass(UserCountReducer.class);
        secondJob.setOutputKeyClass(IntWritable.class);
        secondJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(secondJob, new Path(args[1]));
        FileOutputFormat.setOutputPath(secondJob, new Path(args[2]));
        System.exit(secondJob.waitForCompletion(true) ? 0 : 1);
    }
}