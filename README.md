# 4 core CPU의 멀티 프로세스 현상을 자바로 구현하기

## 필요 개념
### CPU
운영체제는 실행할 프로그램을 메모리에 적재하고, 더 이상 실행되지 않는 프로그램을 메모리에서 삭제하며 지속적으로 메모리 자원을 관리한다. 따라서 운영체제는 최대한 공정하게 여러 프로그램에 CPU 자원을 할당해야 한다. CPU는 실행되는 프로세스들은 실행 순서와 자원의 일관성을 보장해야 하기에 반드시 동기화(synchronization)되어야 한다.

### 프로세스
프로세스(process)란 '실행 중인 프로그램(program)'으로, 컴퓨터에서 연속적으로 실행되고 있는 '동적인 상태'의 컴퓨터 프로그램이다. 프로그램을 실행하면 OS로부터 실행에 필요한 자원(메모리)을 할당받아 프로세스가 된다.

프로세스는 프로그램을 수행하는 데 필요한 데이터와 메모리 등의 자원 그리고 쓰레드로 구성되어 있다.

### 쓰레드
쓰레드(thread)는 프로세스가 할당받은 자원을 이용하는 실행 단위이다. 또한 프로세스의 특정한 수행 경로이자 프로세스 내에서 실행되는 여러 흐름의 단위이다. 

## 코드 실행 방법
1. 'MultiCook.java' 파일을 다운로드한다.
2. terminal창으로 cd 명령어를 사용하여 java파일이 있는 곳으로 이동한다. 
3. ls 명령어를 사용하여 해당 위치에 MultiCook.java 파일이 존재하는지 확인한다.
4. 'javac MultiCook.java' 명령어를 사용하여 컴파일한다.
5. 'java MultiCook `숫자`'를 입력하여 컴파일된 MultiCook.class 파일을 실행한다.

## 코드 설명
4개의 조리대와 4명의 요리사가 있다. 조리대는 CPU를 의미하고, 요리사는 프로세스를 의미한다. 즉, 쿼드 코어(4 core) CPU 상황이다.
들어가기에 앞서 요리사를 의미하는 프로세스를 구현할 때, Thread를 사용하여 구현했다. 그러나 이것은 쓰레드를 의미하는 것이 아닌 프로세스를 의미하는 것이다. 쓰레드는 요리사가 요리를 만드는 행동이다. 요리사 자체가 프로세스이다.

요리사가 물을 끓이면서 채소를 썰거나 밥을 씻고 있다면, 이것은 멀티 쓰레드를 의미한다.
그러나 여러 개의 조리대(n개의 CPU)에서 여러 명의 요리사(m개의 프로세스)가 요리를 만드는 중이라면 멀티 프로세스를 의미한다.

우선 몇인분을 만들어야 할 지 입력받고, 4명의 요리사(프로세스)를 생성한다.
````
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
````
만들어야 할 음식의 갯수를 foodCount에 저장하고, 4개의 조리대(4 Core CPU)를 준비한다.
````
class FoodCook implements Runnable {
    private int foodCount;      // 프로세스의 개수     ex) 처리해야 할 요리 주문 갯수
    private String[] kitchens = {"_", "_", "_", "_"};   // 4 CORE(quad core) CPU    ex) 조리대의 갯수

    public FoodCook(int foodCount) {
        this.foodCount = foodCount;
    }

    ...
}
````
각 프로세스들이 CPU 자원을 선점하여 사용하는 과정이다. 선점을 구현할 때는 synchronized를 사용하여 요리사(프로세스)가 조리대(CPU)를 사용 중일 때, 접근하지 못하도록 구현했다.
````
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
````
CPU 사용 시작과 종료 시, CPU의 상태를 나타내도록 출력했다.
````
private void showKichens() {    // CPU 상태 보기
    StringBuilder sb = new StringBuilder();

    sb.append("                                  ");

    for (int i = 0; i < kitchens.length; i++) {
        sb.append(" ").append(kitchens[i]);
    }

    System.out.println(sb.toString());

}
````
