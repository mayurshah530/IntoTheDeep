package org.firstinspires.ftc.teamcode.mechanisms;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class RobotV1 {
    private static RobotV1 single_instance = null;

    public MecanumDrive drive = null;
    public Arm arm = null;
    public Intake intake = null;
    public Wrist wrist = null;
    public Lift lift = null;

    private RobotV1(HardwareMap hardwareMap, Pose2d initialPose){
        drive = new MecanumDrive(hardwareMap, initialPose);
        arm = new Arm(hardwareMap);
        intake = new Intake(hardwareMap);
        wrist = new Wrist(hardwareMap);
        lift = new Lift(hardwareMap);
    }

    public static synchronized RobotV1 getInstance(HardwareMap hardwareMap, Pose2d initialPose){
        if (single_instance == null){
            single_instance = new RobotV1(hardwareMap,initialPose);
        }
        return single_instance;
    }

    public void reset(){
        arm.reset();
        drive.reset();
        intake.reset();
        wrist.reset();
        lift.reset();
    }

    public Action scoreHighAction(){
        return new SequentialAction(
                arm.armScoreAction(),
                lift.liftUpAction());
    }

    public Action collectAction(){
        return new SequentialAction(
                arm.armGroundCollectAction(),
                intake.intakeAction()
        );
    }

    public Action foldBackAction(){
        return new ParallelAction(
            arm.armfoldbackaction(),
            lift.liftDownAction()
        );
    }

    public Action travelAction() {
        return new SequentialAction(
                arm.armVerticalAction()
                ,lift.liftDownAction()
                ,arm.armRobotTravelAction()
        );
    }
}
