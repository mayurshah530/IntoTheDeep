package org.firstinspires.ftc.teamcode.auto.test;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.RobotV1;


@Config
@Autonomous(name = "\uD83D\uDD34 - RedNearBasketV4", group = "RoadRunner 1.0")
public class RedNearBasketv4 extends LinearOpMode {

    // Start position red near
    Pose2d RED_SCORE_START_POSE = new Pose2d(-38, -60, Math.toRadians(180));

    public static double RED_BASKET_X = -48;
    public static double RED_BASKET_Y = -48;
    public static double RED_BASKET_HEADING = 180+45;

    public static double RED_SAMPLE1_X = -28;
    public static double RED_SAMPLE2_X = -36;
    public static double RED_SAMPLE3_X = -42; // -46 would hit the boundary

    public static double RED_SAMPLE1_Y = -30;
    public static double RED_SAMPLE2_Y = -22;
    public static double RED_SAMPLE3_Y = -22;

    public static double RED_SAMPLE1_HEADING = 161;
    public static double RED_SAMPLE2_HEADING = 180;
    public static double RED_SAMPLE3_HEADING = 180;


    @Override
    public void runOpMode() {

        RobotV1 robot = RobotV1.getInstance(hardwareMap, RED_SCORE_START_POSE);
        robot.reset();
        robot.drive.pose = RED_SCORE_START_POSE;

        // ==== Start of Trajectory actions ====
        TrajectoryActionBuilder startToBasketTab = robot.drive.actionBuilder(RED_SCORE_START_POSE)
                .strafeToLinearHeading(new Vector2d(-38, -56), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING));

        TrajectoryActionBuilder driveBasketToSample1Tab = startToBasketTab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_SAMPLE1_X, RED_SAMPLE1_Y), Math.toRadians(RED_SAMPLE1_HEADING));

        TrajectoryActionBuilder driveSample1ToBasketTab = driveBasketToSample1Tab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING));

        TrajectoryActionBuilder driveBasketToSample2Tab = driveSample1ToBasketTab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_SAMPLE2_X, RED_SAMPLE2_Y), Math.toRadians(180));

        TrajectoryActionBuilder driveSample2ToBasketTab = driveBasketToSample2Tab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING));

        TrajectoryActionBuilder driveBasketToSample3Tab = driveSample2ToBasketTab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_SAMPLE3_X, RED_SAMPLE3_Y), Math.toRadians(180));

        TrajectoryActionBuilder driveSample3ToBasketTab = driveBasketToSample3Tab.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING));

        // ==== End of Trajectory actions ====


        //  ==== Start of composite actions: combine trajectory and non-trajectory actions
        Action startScoreHighAction = new ParallelAction(
                robot.wrist.wristFoldOutAction(),
                robot.scoreHighAction()
        );

        Action cStartToBasketScoreAction = new SequentialAction(
                new ParallelAction(
                        startToBasketTab.build(),
                        startScoreHighAction)
                , new SleepAction(0.1)
                , robot.intake.depositAction());

        Action cBasketToSample1Action = new ParallelAction(
                robot.arm.armRobotTravelAction(),
                robot.lift.liftDownAction(),
                driveBasketToSample1Tab.build()
        );
        Action cBasketToSample2Action = new ParallelAction(
                robot.arm.armRobotTravelAction(),
                robot.lift.liftDownAction(),
                driveBasketToSample2Tab.build()
        );
        Action cBasketToSample3Action = new ParallelAction(
                robot.arm.armRobotTravelAction(),
                robot.lift.liftDownAction(),
                driveBasketToSample3Tab.build()
        );

        Action cSample1ToBasketAction = new ParallelAction(
                driveSample1ToBasketTab.build(),
                robot.scoreHighAction()
        );
        Action cSample2ToBasketAction = new ParallelAction(
                driveSample2ToBasketTab.build(),
                robot.scoreHighAction()
        );
        Action cSample3ToBasketAction = new ParallelAction(
                driveSample3ToBasketTab.build(),
                robot.scoreHighAction()
        );
        // ==== End of composite actions ====

        // TEST: One long trajectory.
        TrajectoryActionBuilder monolith = robot.drive.actionBuilder(RED_SCORE_START_POSE)
                .afterTime(0, robot.wrist.wristFoldOutAction()) // wrist out
                .afterTime(0.2, robot.scoreHighAction())        // arm and lift up
                // drive to score position
                .strafeToLinearHeading(new Vector2d(-38, -56), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING))
                // deposit
                .stopAndAdd(robot.intake.depositAction())
                // get into the travel pose and...
                .afterTime(0, robot.travelAction())
                //  drive to sample 1
                .strafeToLinearHeading(new Vector2d(RED_SAMPLE1_X, RED_SAMPLE1_Y), Math.toRadians(RED_SAMPLE1_HEADING))
                // collect pose, intake on.
                .afterTime(0, robot.collectAction())
                // slide forward to assist with intake
                .setTangent(Math.toRadians(RED_SAMPLE1_HEADING))
                .lineToX(RED_SAMPLE1_X-4)
                // get into the scoring position and...
                .afterTime(0, robot.scoreHighAction())
                // drive to the basket
                .strafeToLinearHeading(new Vector2d(RED_BASKET_X, RED_BASKET_Y), Math.toRadians(RED_BASKET_HEADING))
                // deposit
                .stopAndAdd(robot.intake.depositAction())
                // ====Begin Hack: to make testing easier, go back to the starting position.
                .afterTime(0, robot.foldBackAction())
                .strafeToLinearHeading(RED_SCORE_START_POSE.position, Math.toRadians(180))
                // === End Hack.
                .endTrajectory();
        Action monolithAction = monolith.build();

        while(!isStopRequested() && !opModeIsActive()) {
            // Wait for the start signal
        }

        waitForStart();

        if (isStopRequested());

        Actions.runBlocking(
                new SequentialAction(
//                        monolithAction
                        //
//                        cBasketToSample1Action,
//                        collectAction,
//                        robot.arm.armRobotTravelAction(),
//                        cSample1ToBasketAction,
//                        new SleepAction(0.1),
//                        robot.intake.depositAction(),
//                        //
//                        cBasketToSample2Action,
//                        robot.arm.armGroundCollectAction(),
//                        new SleepAction(0.5),
//                        robot.arm.armRobotTravelAction(),
//                        cSample2ToBasketAction,
//                        new SleepAction(0.1),
//                        robot.intake.depositAction(),
//                        //
//                        cBasketToSample3Action,
//                        robot.arm.armGroundCollectAction(),
//                        new SleepAction(0.5),
//                        robot.arm.armRobotTravelAction(),
//                        cSample3ToBasketAction,
//                        new SleepAction(0.1),
//                        robot.intake.depositAction(),
//                        //
//                        foldBackAction
                )
        );

    } // runOpMode



}

