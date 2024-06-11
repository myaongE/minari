package com.example.demo.discordbot;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class DiscordListener extends ListenerAdapter {

    private static final String PREFIX = "!";
    private boolean isCensorshipEnabled = true; // 검열 기능 활성화 여부

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Message message = event.getMessage();
        String content = message.getContentDisplay().trim();

        // 명령어 처리 전에 'content'를 확인하여 필요한 작업 수행
        if (isCensorshipEnabled) {
            // 메시지 검열 및 삭제
            checkAndDeleteMessage(content, message);
        }

        // '!삭제' 명령어 처리
        if (content.equals(PREFIX + "삭제")) {
            // 최신 메시지를 삭제하는 작업을 반복
            textChannel.getHistory().retrievePast(100).queue(messages -> {
                // 메시지 ID 수집
                List<String> messageIds = messages.stream()
                        .map(Message::getId)
                        .collect(Collectors.toList());

                // 메시지 삭제
                textChannel.deleteMessagesByIds(messageIds).queue(
                        success -> {
                            textChannel.sendMessage("99개의 메세지가 삭제 완료되었습니다.").queue();
                            // 메시지 삭제가 성공하면 아무 작업도 하지 않습니다.
                        },
                        failure -> {
                            // 메시지 삭제에 실패하면 에러 메시지 출력
                            log.error("메시지 삭제에 실패하였습니다: {}", failure.getMessage());
                        }
                );
            });
            return;
        }

        // 명령어 처리를 위해 'content'가 PREFIX로 시작하는지 확인
        if (!content.startsWith(PREFIX)) {
            // PREFIX로 시작하지 않으면 명령어가 아니므로 종료
            return;
        }

        // 'content'에서 명령어 부분을 추출하여 'command' 변수에 할당
        String command = content.substring(PREFIX.length());

        // 명령어 처리
        log.info("Received message: {}", content);
        if (user.isBot() || content.isEmpty()) {
            // 봇이거나 내용이 비어있으면 처리하지 않음
            return;
        }

        // 각 명령어에 따라 작업을 수행
        if (command.equals("욕활성화")) {
            isCensorshipEnabled = true;
            textChannel.sendMessage("비속어 검열 기능이 활성화되었습니다.").queue();
            return;
        }

        if (command.equals("욕비활성화")) {
            isCensorshipEnabled = false;
            textChannel.sendMessage("비속어 검열 기능이 비활성화되었습니다.").queue();
            return;
        }

        // 나머지 명령어들을 처리하는 부분
        String returnMessage = sendMessage(event, command);
        textChannel.sendMessage(returnMessage).queue();
    }




    private String sendMessage(MessageReceivedEvent event, String message) {
        User user = event.getAuthor();
        String returnMessage;

        switch (message) {
            case "도움":
                returnMessage = user.getAsMention()+ "님 미나리 사용법입니다! \n'!'뒤로 띄어쓰기 없이 명령어를 입력해주시면 됩니다.\n\n "
                		+ "'!안녕하세요' : 미나리가 인사합니다 \n "
                		+ "'!정보' : 미나리의 정보 \n "
                		+ "'!주목' : 모두의 이목을 끌 수 있습니다 \n"
                		+ "'!삭제' : 99개의 메세지를 삭제할 수 있습니다 \n"
                		+ "'!1' : 숫자 1 \n"
                		+ "'!2' : 숫자 2 \n"
                		+ "'!전주원은' : 무엇일까요? \n"
                		+ "'!욕활성화' : 비속어 검열 기능을 활성화합니다 \n"
                		+ "'!욕비활성화' : 비속어 검열 기능을 비활성화합니다";
                break;
            case "안녕하세요":
                returnMessage = user.getAsMention() + "님 안녕하세요! 좋은 하루 되세요";
                break;
            case "정보":
                returnMessage = user.getAsMention() + "님 저는 미나리입니다!!";
                break;
            case "1":
                returnMessage = user.getAsMention() + "1?";
                break;
            case "2":
                returnMessage = user.getAsMention() + "yee~~~~!";
                break;
            case "주목":
                returnMessage = "@everyone \n\n\n" + "🚨🚨🚨🚨🚨애애애애애앵‼️‼️‼️‼️‼️‼️🚨🚨🚨🚨🚨🚨📢📢📢📢📢📢📢긴급상황‼️‼️‼️긴급상황‼️‼️‼️‼️‼️📢📢📢📢📢📢📢🔊🔊🔊🔊🔊🔊‼️‼️‼️🔊🔊🔊🔊🔊🔊🔊🔊🔊🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🛎🛎🛎🛎🛎🛎🛎모두주목해주세요땡땡땡땡땡🛎🛎🛎🛎🛎🎤🎤🎤🎤아아-마이크테스트-!마이크테스트-!🎤🎤🎤🎤🎙🎙🎙🎙🎙마이크테스트— 들린다면 응답하라—-🎙🎙🎙🎙🎙📣📣📣📣";
                break;
            case "전주원은":
                returnMessage = "바보~";
                break;
            default:
                returnMessage = "못 알아 듣겠어요 죄송합니다.";
                break;
        }
        return returnMessage;
    }

    public static void checkAndDeleteMessage(String content, Message message) {
        // 욕설을 포함한 정규 표현식 패턴
        String pattern = "(10새|10새기|10새리|10세리|10쉐이|10쉑|10스|10쌔| 10쌔기|10쎄|10알|10창|10탱|18것|18넘|18년|18노|18놈|18뇬|18럼|18롬|18새|18새끼|18색|18세끼|18세리|18섹|18쉑|18스|18아|ㄱㅐ|ㄲㅏ|ㄲㅑ|ㄲㅣ|ㅅㅂㄹㅁ|ㅅㅐ|ㅆㅂㄹㅁ|ㅆㅍ|ㅆㅣ|ㅆ앙|ㅍㅏ|凸| 갈보|갈보년|강아지|같은년|같은뇬|개같은|개구라|개년|개놈|개뇬|개대중|개독|개돼중|개랄|개보지|개뻥|개뿔|개새|개새기|개새끼|개새키|개색기|개색끼|개색키|개색히|개섀끼|개세|개세끼|개세이|개소리|개쑈|개쇳기|개수작|개쉐|개쉐리|개쉐이|ㅅ|개쉑|개쉽|개스끼|개시키|개십새기| 개십새끼|개쐑|개씹|개아들|개자슥|개자지|개접|개좆|개좌식|개허접|걔새|걔수작|걔시끼|걔시키|걔썌|걸레|게색기|게색끼|광뇬|구녕|구라|구멍|그년|그새끼|냄비|놈현|뇬|눈깔|뉘미럴|니귀미|니기미|니미|니미랄|니미럴|니미씹|니아배|니아베|니아비|니어매|니어메|니어미|닝기리|닝기미|대가리|뎡신|도라이|돈놈|돌아이|돌은놈|되질래|뒈져|뒈져라|뒈진|뒈진다|뒈질| 뒤질래|등신|디져라|디진다|디질래|딩시|따식|때놈|또라이|똘아이|똘아이|뙈놈|뙤놈|뙨넘|뙨놈|뚜쟁|띠바|띠발|띠불|띠팔|메친넘|메친놈|미췬| 미췬|미친|미친넘|미친년|미친놈|미친새끼|미친스까이|미틴|미틴넘|미틴년| 미틴놈|바랄년|병자|뱅마|뱅신|벼엉신|병쉰|병신|부랄|부럴|불알|불할|붕가|붙어먹|뷰웅|븅|븅신|빌어먹|빙시|빙신|빠가|빠구리|빠굴|빠큐|뻐큐|뻑큐|뽁큐|상넘이|상놈을|상놈의|상놈이|새갸|새꺄|새끼|새새끼|새키|색끼|생쑈|세갸|세꺄|세끼|섹스|쇼하네|쉐|쉐기|쉐끼|쉐리|쉐에기|쉐키|쉑|쉣|쉨|쉬발|쉬밸|쉬벌|쉬뻘|쉬펄|쉽알|스패킹|스팽|시궁창|시끼|시댕|시뎅|시랄|시발|시벌|시부랄|시부럴|시부리|시불|시브랄|시팍|시팔|시펄|신발끈|심발끈|심탱|십8|십라|십새|십새끼|십세|십쉐|십쉐이|십스키|십쌔|십창|십탱|싶알|싸가지|싹아지|쌉년|쌍넘|쌍년|쌍놈|쌍뇬|쌔끼| 쌕|쌩쑈|쌴년|썅|썅년|썅놈|썡쇼|써벌|썩을년|썩을놈|쎄꺄|쎄엑| 쒸벌|쒸뻘|쒸팔|쒸펄|쓰바|쓰박|쓰발|쓰벌|쓰팔|씁새|씁얼|씌파|씨8| 씨끼|씨댕|씨뎅|씨바|씨바랄|씨박|씨발|씨방|씨방새|씨방세|씨밸|씨뱅|씨벌|씨벨|씨봉|씨봉알|씨부랄|씨부럴|씨부렁|씨부리|씨불|씨붕|씨브랄| 씨빠|씨빨|씨뽀랄|씨앙|씨파|씨팍|씨팔|씨펄|씸년|씸뇬|씸새끼|씹같|씹년|씹뇬|씹보지|씹새|씹새기|씹새끼|씹새리|씹세|씹쉐|씹스키|씹쌔|씹이|씹자지|씹질|씹창|씹탱|씹퇭|씹팔|씹할|씹헐|아가리|아갈|아갈이|아갈통|아구창|아구통|아굴|얌마|양넘|양년|양놈|엄창|엠병|여물통|염병|엿같|옘병|옘빙|오입|왜년|왜놈|욤병|육갑|은년|을년|이년|이새끼|이새키|이스끼|이스키|임마|자슥|잡것|잡넘|잡년|잡놈|저년|저새끼|접년|젖밥|조까|조까치|조낸|조또|조랭|조빠|조쟁이|조지냐|조진다|조찐|조질래|존나|존나게|존니|존만| 존만한|좀물|좁년|좆|좁밥|좃까|좃또|좃만|좃밥|좃이|좃찐|좆같|좆까|좆나|좆또|좆만|좆밥|좆이|좆찐|좇같|좇이|좌식|주글|주글래|주데이|주뎅|주뎅이|주둥아리|주둥이|주접|주접떨|죽고잡|죽을래|죽통|쥐랄|쥐롤|쥬디|지랄|지럴|지롤|지미랄|짜식|짜아식|쪼다|쫍빱|찌랄|창녀|캐년|캐놈|캐스끼|캐스키|캐시키|탱구|팔럼|퍽큐|호로|호로놈|호로새끼|호로색|호로쉑|호로스까이|호로스키|후라들|후래자식|후레|후뢰|씨ㅋ발|ㅆ1발|씌발|띠발|띄발|뛰발|띠ㅋ발|뉘뮈)";
        // 패턴을 정규 표현식 객체로 컴파일
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        // 주어진 문자열에서 정규 표현식과 일치하는 부분을 찾기 위한 Matcher 생성
        Matcher m = p.matcher(content);
        
        // 정규 표현식과 일치하는 부분이 있는지 확인합니다.
        if (m.find()) {
            // 금지어와 일치하면 메시지 삭제 및 경고 메시지 전송
            // 메시지를 가져와서 존재하는지 확인
            Message retrievedMessage = message.getChannel().retrieveMessageById(message.getId()).complete();
            // 메시지가 존재하면 삭제
            if (retrievedMessage != null) {
                retrievedMessage.delete().queue(
                    success -> {
                        // 메시지 삭제가 성공하면 경고 메시지 전송
                        message.getChannel().sendMessage("경고! 메시지에 부적절한 내용이 있어 삭제하였습니다.").queue();
                    },
                    failure -> {
                        // 메시지 삭제에 실패하면 에러 메시지 출력
                        log.error("메시지 삭제에 실패하였습니다: {}", failure.getMessage());
                    }
                );
            }
        } else {
            // 금지어가 발견되지 않았으므로 메시지 유지
            // 이 부분에 추가적인 로직을 넣을 수 있습니다.
        }
    }

}