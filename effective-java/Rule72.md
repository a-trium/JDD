# 규칙 72. 스레드 스케줄러에 의존하지 마라

스레드 스케줄러(thread scheduler) : 실행할 스레드가 많을때, 어떤 스레드가 얼마나 오랫동안 실행할지 결정하는 것

> OS에 따라 스케줄링 정책이 바뀔 수 있으므로, 스레드 스케줄러에 의존하는 프로그램은 이식성이 떨어진다.

안정적이고, 즉각 반응하며 이식성 좋은 프로그램을 만드는 방법
- `실행 가능 스레드`의 평균적 수가 `프로세서`수보다 너무 많아 지지 않도록 하는것 (대기중(waiting)인 스레드는 실행 가능한 스레드가 아님)

실행 가능 스레드수를 일정 수준으로 낮추는 방법
- 각 스레드가 필요한 일을 하고 나서 다음에 할 일을 기다리게 만듬
- 스레드가 일을하고 있지 않을 때 실행 중이어서는 안됨

> 각 스레드의 태스크가 너무 작아질 경우, 태스크 실행 오버헤드가 걸려 성능에 악영향을 끼침

그밖에..
1. 바쁜대기(busy wait)하는 스레드를 이용하는 프로그램은 스케줄러 변화에 취약함
2. 바쁜대기(busy wait)는 프로세서에 높은 부담을 주며, 다른 스레드가 할 수 있는 일에 영향을 미침
3. 충분한 CPU 시간을 할당을 받지 못하는 스레드가 있는 프로그램이라도 `Thread.yield`를 호출해서 문제를 해결하려하지 마라
4. ***스레드 우선순위는 자바 플랫폼에서 가장 이식성이 낮은 부분*** 이므로 우선수위를 변경하는 기법을 피해라