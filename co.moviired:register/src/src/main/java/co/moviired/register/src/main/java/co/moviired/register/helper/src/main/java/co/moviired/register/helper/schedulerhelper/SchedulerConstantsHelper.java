package co.moviired.register.helper.schedulerhelper;

public final class SchedulerConstantsHelper {

    public static final String PROJECT_PATH = "${spring.application.root}";
    public static final String SCHEDULER_HELPER_PATH = "${scheduler-helper.pathRequestNextTime}";
    public static final String SCHEDULER_HELPER_API = "scheduler-helper-api";
    public static final String SCHEDULER_PROPERTIES_PREFIX = "scheduler-helper";
    public static final String LOG_THIRD_REQUEST = "INVOKE {}: {URL: '{}', body : '{}'}";
    public static final String LBL_RESPONSE = "{} RESPONSE - Value [{}]";
    public static final String LBL_ERROR = "{} ERROR RESPONSE - VALUE  [{}]";
    public static final String SCHEDULER_HELPER_COMPONENT = "SCHEDULER HELPER";
    public static final String EMPTY_STRING = "";
    public static final String PROCESS_PLACE_HOLDER = "{process}";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String COMPONENT = "component";
    public static final String STRING_LINE = "-";
    public static final String TWO_DOTS = ":";
    public static final String LOG_NEXT_EXECUTION = "NEXT EXECUTION OF PROCESS {}: {} WAITING {} MILLISECONDS";
    public static final String REGEX_CLEAN_PROCESS = "[^a-z0-9]";
    public static final String ASSIGN_NEXT_EXECUTION = "ASSIGN NEXT EXECUTION FOR PROCESS {} MINIMUM TIME BETWEEN INSTANCES {}";
    public static final String SUCCESS_NEXT_EXECUTION = "NEXT TIME FOR EXECUTE PROCEES {} IS {}";
    public static final String ERROR_ASSIGN_TIME = "ERROR ASSIGN TIME FOR PROCESS {}";

    private SchedulerConstantsHelper() {
        super();
    }
}
