// 실행 방법
// 1. 'MultiCook.java' 파일을 다운로드한다.
// 2. terminal창으로 cd 명령어를 사용하여 java파일이 있는 곳으로 이동한다.
// 3. ls 명령어를 사용하여 해당 위치에 MultiCook.java 파일이 존재하는지 확인한다.
// 4. 'javac MultiCook.java' 명령어를 사용하여 컴파일한다.
// 5. 'java MultiCook [숫자]'를 입력하여 컴파일된 MultiCook.class 파일을 실행한다.

public class MultiCook {
    public static void main(String[] args) {
        try {
            FoodCook foodCook = new FoodCook(Integer.parseInt(args[0]));

            new Thread(foodCook, "A").start();  // process
            new Thread(foodCook, "B").start();  // process
            new Thread(foodCook, "C").start();  // process
            new Thread(foodCook, "D").start();  // process
        } catch (Exception e) {     // 숫자입력이 아닌 경우 예외 처리
            e.printStackTrace();
        }
    }
}

class FoodCook implements Runnable {
    private int foodCount;      // 프로세스의 개수     ex) 처리해야 할 요리 주문 갯수
    private String[] kitchens = {"_", "_", "_", "_"};   // 4 CORE(quad core) CPU    ex) 조리대의 갯수

    public FoodCook(int foodCount) {
        this.foodCount = foodCount;
    }

    @Override
    public void run() {     // 프로세스가 실행될 때 수행할 코드
        while (foodCount >= 1) {

            // synchronized를 이용하여 프로세스들의 동시 접근을 방지한다.
            // ex) 두 명의 요리사가 요리를 동시에 시작했는데 foodCount -= 2 가 아닌, foodCount -= 1인 상황 방지
            synchronized (this) {
                // 처리해야 할 프로세스의 개수
                // ex) 만들어야 할 요리의 갯수
                System.out.println("-------------------" + foodCount + "인분 남음-------------------");
                System.out.println();
                foodCount--;
            }

            for (int i = 0; i < kitchens.length; i++) {
                if (!kitchens[i].equals("_")) continue;

                synchronized (this) {
                    // 요리하는 동안(2s) 해당 프로세스가 CPU 사용중
                    kitchens[i] = Thread.currentThread().getName();

                    // 프로세스가 (i + 1)번 CPU에서 실행된다.
                    // ex) 요리가 (i + 1)번 조리대에서 실행된다.
                    System.out.println(Thread.currentThread().getName() + " 요리사: " + (i + 1) + "번 주방 사용 시작(2s)");

                    // CPU 상태 보기
                    showKichens();
                }

                try {
                    // 프로세스 처리중(2초 소요)
                    // 이 부분을 랜덤한 시간으로 수정한다면, 각 프로세스 내 스레드의 처리 과정을 실제 환경과 동일하게 사용할 수 있다.
                    // ex) 요리중
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    kitchens[i] = " ";
                }

                synchronized (this) {
                    kitchens[i] = "_";

                    // 프로세스가 처리되었다.
                    System.out.println(Thread.currentThread().getName() + " 요리사 주방 사용 종료");

                    // CPU 상태 보기
                    showKichens();
                }
                break;
            }

            try {
                // 0~1초동안 프로세스 일시 정지(랜덤 시간 대기)
                // 이 부분을 랜덤한 시간으로 수정한다면, CPU 내 프로세스의 처리 과정을 실제 환경과 동일하게 사용할 수 있다.
                // ex) 요리사가 요리를 완성하고 재료를 가지러 간다던지 손을 씻는다던지 하는 행위
                Thread.sleep(Math.round(1000 * Math.random()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showKichens() {    // CPU 상태 보기
        StringBuilder sb = new StringBuilder();

        sb.append("                                  ");

        for (int i = 0; i < kitchens.length; i++) {
            sb.append(" ").append(kitchens[i]);
        }

        System.out.println(sb.toString());

    }
}
