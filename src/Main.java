import java.util.Scanner;

public class Main {

    // global variables
    private static float probabilityCancel = 0.8f;  // вероятность выпадения отказа первого уровня
    private static float tCancelAverage = 1f;     // среднее время безотказной работы системы (время между началом текущей заявки и окончанием предыдущей)
    private static float tFixAverage = 1f;        // среднее время устранения отказа
    private static float applicationIntensity = 5f; // интенсивность потока заявок (переодичность поступления заявок)
    private static float serviceIntensity = 1f;     // интенсивность потока обслуживания зявок
    private static float systemLifeTime = 10f;      // период работы системы

    // cancel variables
    private static float cancelStartTime = 0f;
    private static float cancelFinishTime = 0f;
    private static int cancelType = 0;

    // application variables
    private static float applicationStartTime = 0f;
    private static float applicationFinishTime = 0f;

    private static boolean isNeededLog = false; // переменная использующая для вывода логов

    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Для ручного определения переменных введите 1;");
        System.out.println("Для автоматического определения переменных введите 0.");
        int choose = scanner.nextInt();

        System.out.print("Нужны ли вам логи? Если да то введите 1, если нет, то ноль: ");
        if (scanner.nextInt() == 1) {
            isNeededLog = true;
            System.out.println("Пояснение к выводу логов.");
            System.out.println("App (Заявка): {Начальяное время заявки} {Время обсулживания заявки} " +
                    "{Время завершения обслуживания заявки}");
            System.out.println("Can (Отмена): {Начальяное время отказа} {Время обсулживания отказа} " +
                    "{Время завершения обслуживания отказа} {Тип отказа}");
        }

        System.out.println("Fin (Окончательное время обработки): {Начальяное время заявки} " +
                "{Время обсулживания заявки} {Время завершения обслуживания заявки}");

        if (choose == 1) {
            defineVariables();
        }

        cancel();
        while (applicationFinishTime <= systemLifeTime) {
            application();
            service();
        }
    }

    private static void defineVariables() {
        System.out.print("Введите вероятность выпадения отказа первого уровня: ");
        probabilityCancel = scanner.nextFloat();
        System.out.print("Введите среднее время безотказной работы системы: ");
        tCancelAverage = scanner.nextFloat();
        System.out.print("Введите среднее время устранения отказа: ");
        tFixAverage = scanner.nextFloat();
        System.out.print("Введите интенсивность потока заявок: ");
        applicationIntensity = scanner.nextFloat();
        System.out.print("Введите интенсивность потока обслуживания: ");
        serviceIntensity = scanner.nextFloat();
        System.out.print("Введите период работы системы: ");
        systemLifeTime = scanner.nextFloat();
    }

    private static void cancel() {
        // define cancel type
        float z = (float) Math.random(); // ДСЧ
        if (z < probabilityCancel) {
            cancelType = 1;
        } else {
            cancelType = 2;
        }

        // define cancel start time
        z = (float) Math.random(); // ДСЧ
        cancelStartTime = cancelFinishTime - (float) (tCancelAverage * Math.log(z));

        // define cancel finish time
        z = (float) Math.random(); // ДСЧ
        cancelFinishTime = cancelStartTime - (float) (tFixAverage * Math.log(z));
    }

    private static void application() {
        float z;

        // define application start time
        z = (float) Math.random(); // ДСЧ
        applicationStartTime = applicationFinishTime - (float) (Math.log(z) / applicationIntensity);

        // define application finish time
        z = (float) Math.random(); // ДСЧ
        applicationFinishTime = applicationStartTime - (float) (Math.log(z) / serviceIntensity);
    }

    private static void service() {
        if (isNeededLog) {
            System.out.println();
            System.out.println("App: " + applicationStartTime + " " + (applicationFinishTime - applicationStartTime) + " " + applicationFinishTime);
            System.out.println("Can: " + cancelStartTime + " " + (cancelFinishTime - cancelStartTime) + " " + cancelFinishTime + " " + cancelType);
        }

        float f = analyze(); // analyze
        if (f == 1) {
            // I don't know what
        } else if (f == 2 && cancelType == 1) {
            applicationFinishTime = applicationFinishTime + cancelFinishTime - cancelStartTime;
        } else if ((f == 2 && cancelType == 2) || f == 3) {
            applicationFinishTime = cancelFinishTime + applicationFinishTime - applicationStartTime;
            applicationStartTime = cancelFinishTime;
        }

        System.out.println("Fin: " + applicationStartTime + " " + (applicationFinishTime - applicationStartTime) + " " + applicationFinishTime);

        cancel();
    }

    /**
     * @return 1 - отказ возник после конца обслуживания заявки; 2 - отказ возник когда началась обработка заявки; 3 - отказ возник перед началом обрабоки заявки, но его устранение не было завершено до начала обработки этой же заявки; 4 - в остальных случаях.
     */
    private static int analyze() {
        if (applicationFinishTime < cancelStartTime) return 1;
        else if (applicationStartTime < cancelStartTime && cancelStartTime < applicationFinishTime) return 2;
        else if (cancelStartTime < applicationStartTime && applicationStartTime < cancelFinishTime) return 3;
        else return 4;
    }
}