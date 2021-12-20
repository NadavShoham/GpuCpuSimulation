package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ActivateNextModelBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {

        // create gson instance and read content of json file into input instance
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Input input = null;
        try(Reader reader = new FileReader(args[0])) {
            input = gson.fromJson(reader, Input.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;

        // Objects
        List<Student> students = new LinkedList<>();
        List<ConfrenceInformation> conferences = new LinkedList<>();
        List<GPU> gpus = new LinkedList<>();
        List<CPU> cpus = new LinkedList<>();
        Cluster cluster = Cluster.getInstance();

        // All microServices and messageBus
        MessageBusImpl bus = MessageBusImpl.getInstance();
        List<MicroService> microServices = new LinkedList<>();
        TimeService timeService = new TimeService(input.getTickTime(), input.getDuration());
        List<Thread> threads = new LinkedList<>();

        // Creates students, models and studentServices
        for (InputStudent inputStudent:input.getStudents()) {
            Student student = new Student(inputStudent.getName(), inputStudent.getDepartment(), inputStudent.getStatus());
            List<Model> studentModels = new LinkedList<>();
            for (InputModel inputModel: inputStudent.getModels()) {
                Data data = new Data(inputModel.getType(), inputModel.getSize());
                studentModels.add(new Model(inputModel.getName(), data, student));
            }
            student.setModels(studentModels);
            students.add(student);
            microServices.add(new StudentService("StudentService_" + student.getName(), student));
        }

        // Creates GPUs and GPUServices
        int i = 0;
        for (String inputType: input.getGPUS()) {
            GPU gpu = new GPU(inputType, cluster, i);
            gpus.add(gpu);
            GPUService gpuService = new GPUService("GpuService_" + i, gpu);
            microServices.add(gpuService);
            gpu.setGpuService(gpuService);
            i++;
        }
        cluster.setGpus(gpus);

        // Creates CPUs and CPUServices
        i = 0;
        for (int inputType: input.getCPUS()) {
            CPU cpu = new CPU(inputType, cluster, i);
            cpus.add(cpu);
            microServices.add(new CPUService("CpuService_" + i, cpu));
            i++;
        }
        cluster.setCpus(cpus);

        // Create conferences and conferenceServices
        for (InputConference inputConference: input.getConferences()) {
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(inputConference.getName(), inputConference.getDate());
            conferences.add(confrenceInformation);
            ConferenceService conferenceService = new ConferenceService("ConferenceService_" + inputConference.getName(), confrenceInformation);
            confrenceInformation.setConferenceService(conferenceService);
            microServices.add(conferenceService);
        }

        for (MicroService service: microServices) {
            Thread thread = new Thread(null, service, service.getName());
            threads.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // TODO print func
        /*
        int num = 0;
        for (MicroService microService: bus.getDeleteMe().get(TrainModelEvent.class)) {
            System.out.println(microService.getName() + " subscribed " + num + " place to Train");
            num++;
        }
        num = 0;
        for (MicroService microService: bus.getDeleteMe().get(TestModelEvent.class)) {
            System.out.println(microService.getName() + " subscribed " + num + " place to Test");
            num++;
        }
         */

        Thread thread = new Thread(null, timeService, timeService.getName());
        threads.add(thread);

        thread.start();

        // all MS have been initialized
        bus.sendBroadcast(new ActivateNextModelBroadcast());

        // wait for everyone to finish
        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // create output file
        Output output = new Output();

        //updating the students field
        OutputStudent[] outputStudents = new OutputStudent[students.size()];
        int j = 0;
        for (Student student: students)
            outputStudents[j++] = student.getOutputStudent();
        output.setStudents(outputStudents);

        //updating the conferences field
        OutputConference[] outputConferences = new OutputConference[conferences.size()];
        int k = 0;
        for (ConfrenceInformation conference: conferences)
            outputConferences[k++] = conference.getOutputConference();
        output.setConferences(outputConferences);

        //updating the fields from the statistics
        Statistics statistics = cluster.getStatistics();
        output.setBatchesProcessed(statistics.getNumOfProcessedDataPatches().intValue());
        output.setGpuTimedUsed(statistics.getNumOfGPUTimeUnits().intValue());
        output.setCpuTimedUsed(statistics.getNumOfCPUTimeUnits().intValue());


        // write to file
        try (Writer writer = new FileWriter("Output.json")){
            gson.toJson(output, writer);
            //gson.toJson(output, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

}
