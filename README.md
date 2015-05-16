##<i>MRY</i> Project
Music Recommendation with <i>Youtube</i>.

###Overview 목차 

1. Project Description 프로젝트 설명
2. System architecture 시스템 구조
3. Issues 예상 가능 문제점 및 고려사항
4. Schedule 일정


###1. Project Description 프로젝트 설명
&nbsp;&nbsp;&nbsp;&nbsp;The number of people listening musics from online streaming service is getting higher nowadays.
The musics recommended from each service are variable and close to people's taste. <br>
&nbsp;&nbsp;&nbsp;&nbsp;One of the services that let us search and listen musics without payment is <i>Youtube</i>.
Actually many people are not really watching videos but doing something else such as web surfing or coding.
It means <i>Youtube</i> is being closer to music contents service.
<i>MRY</i> starts from the point. If we give similiar musics while they watch music videos(including not official),
it may be better for them. 
Already <i>Youtube</i> has 'Suggestion' service with their algorithm but it considers watching patterns, 
uploader's another videos, play lists, and more but does not care about music contents itself.<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;<i>MRY</i> Project 는 Music Recommendation with <i>Youtube</i> Project 이다.
음악을 직접 소유하지 않고 인터넷을 통해 제공되는 스트리밍 서비스를 이용하는 사용자는 날이 갈수록 증가하고 있다. 
자신이 소유한 제한된 수의 음악이 아닌 온라인 스트리밍 서비스를 통해 제공되는 음악은 각 서비스의 특성에 따라 다양하고 매 순간마다 변화하며, 사용자의 취향에도 가깝게 추천되고 있다. 
이러한 많은 서비스 중에서도 우리가 원하는 음악을 무료로 쉽게 찾아 감상할 수 있는 Youtube가 있다. 실제로 많은 사람들이 Youtube를 영상 시청이 아닌 음악 감상용으로 사용하고 있는 만큼 우리는 음악 추천 엔진을 통해 Youtube의 음악 영상으로부터 그와 유사한 음악을 담고 있는 다른 영상을 추천해주는 서비스를 제공하려고 한다.
이미 Youtube에서는 Suggestion 메뉴를 통해 사용자들에게 유사한 영상들을 추천해 주고 있지만, 이는 누군가가 재생 목록으로 미리 등록해 놓은 영상들을 보여주거나 사용자들이 어떤 영상을 같이 시청하였는지 동작 패턴을 파악하여 만들어진 목록이다. 즉, 음악적인 유사성이 고려되지 않은 추천기능이라고 생각해 볼 수 있다. 또한 대부분의 각 재생 목록들은 인기 있는 곡들만을 모아두었기 때문에 금방 지루해질 수 있다는 단점이 있다.

###2. System architecture 시스템 구조
&nbsp;&nbsp;&nbsp;&nbsp;크게 Client, Server, Youtube API, Bonacell Music Recommendation Engine으로 구성된다.
Client는 자신이 원하는 곡에 대한 키워드를 전달한다.
Server는 Client로부터 받은 키워드를 사용해 Youtube에서 검색한 후 그 결과를 Client에게 돌려주고, Client가 선택한 곡에 대한 정보를 Bonacell Engine으로 전달하여 유사곡 목록을 얻는다. 마지막으로 유사곡 목록의 각각 곡들을 Youtube에서 검색한 뒤 그 결과를 Client에게 보여준다.

###3. Issues 예상가능 문제점 및 고려사항
&nbsp;&nbsp;&nbsp;&nbsp;프로젝트를 구상함에 있어 몇 가지 제한사항이 존재한다. <br><br>
&nbsp;&nbsp;&nbsp;&nbsp;첫째로 Bonacell DB가 가지고 있지 않은 곡은 추천할 수 없다. 이를 위해서는 현재 많은 스트리밍 서비스에서 소유하고 있는 음원들에 대한 별도의 계약이 필요할 것으로 본다. 다른 방법으로는 추가적으로 SoundCloud API를 활용하여 사용자가 직접 자신이 소유한 음악을 서버로 업로드 할 수 있는 기능을 사용하면 어느 정도 음원 확보에 도움이 될 것으로 예상된다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;둘째로 서비스의 속도가 느려질 수 있다. 1)사용자의 검색 정보를 사용한 Youtube검색 – 2)선택된 곡에 대한 Bonacell Engine검색 – 3)추천 곡들로 다시 Youtube검색 의 과정을 거치기 때문에 이 과정에서 속도 저하 문제가 발생 할 수 있다. 속도 향상을 위해서는 사용자의 검색 히스토리를 따로 저장하여 재 사용하거나, 한번 검색된 결과에 대해서는 유사곡 집합으로 따로 저장해두는 방법이 있다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;셋째로 비용에 대한 문제가 발생할 수 있다. Youtube는 무료 동영상 스트리밍 서비스이기 때문에 Youtube영상을 유료로 제공하는 서비스를 할 수 없다. 하지만 현 상황에 따르면 DB최신화를 위해선 주기적인 음원 구매가 필요하다. 이 경우를 대비해 수익창출모델을 탐색할 필요가 있다.

###4. Schedule 일정
&nbsp;&nbsp;&nbsp;&nbsp;일정은 다음과 같다.

1. Sign up with Google<br>
2. Sign in with Google<br>
3. Setting up server and main page<br>
1st result >> App main page and sign up/sign in with Google API.<br>
1차 결과물 >> App main 페이지 및 Google API 사용한 가입 및 로그인 기능. <br>

4. Enabling searching with Youtube<br>
5. Learning Bonacell Music API<br>
6. Connecting to Engine<br>
7. Showing recommendations<br>
2nd result >> Enabling <i>Youtube</i> video searching with keywords which is given from client. Showing similiar musics list from <i>Bonacell</i> to client.<br>
2차 결과물 >> Client로부터 받은 키워드를 사용해 Youtube검색 가능. Bonacell Engine으로부터 받은 유사 음악리스트를 Client에게 보여주기.<br>

8. Debugging and Testing<br>
9. Setting up to open<br>
3rd result >> final App.<br>
3차 결과물 >> 최종 App.<br>
