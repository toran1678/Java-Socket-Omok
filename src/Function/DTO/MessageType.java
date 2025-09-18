package Function.DTO;

public enum MessageType {
    CreateRoom,
    ResetUserList,
    UpdateUserList, // 리스트를 넘겨서 유저 리스트 업데이트할 것
    ResetRoomList,
    RemoveRoom,
    RemoveUser,
    EnterRoom,
    SearchUser,
    ShowUserInfo,
    LeaveRoom,
    AdminTool,
    UserLeft, // 유저 나감
    Whisper, // 귓속말
    SEARCH_REQUEST,  // 검색 요청
    SEARCH_RESPONSE, // 검색 응답
    EnterAlonePrivateRoom,
    InvitePrivateRoom,
    EnterPrivateRoom, // 1대1 채팅
    PrivateChat, // 1대1 채팅
    OneToOneChatRequest, // 1대1 채팅 (요청한 사람:받는 사람)
    PrivateChatLoadMessage, // 1대1 채팅 내용 불러오기
    EnterUser,
    LeaveUser,
    EMOJI,
    FileTransferRequest,
    FileDownloadRequest,
    FileData,
    FileTransfer, // (roomName:sender:fileName:fileSize)
    CHAT,

    /* 유저가 정보 수정 후 호출 */
    EditUser,

    /* 오목 관련 TYPE */
    OmokEnterRoom, // 오목 방 입장 클라 (currentUser : RoomName), 서버 (currentUser : RoomName : PLAYER or OBSERVER)
    OmokUpdateRoomUserList, // 오목 방 유저 리스트 업데이트 object<List>
    OmokCreateRoom, // 오목 방 생성, 생성한 사람이 바로 들어가고 생성한 사람이 나가면 방 제거 (Creator:RoomName, Object)
    OmokLeaveRoom, // 오목 방에서 유저가 나감 (sender:roomName:PLAYER or OBSERVER)
    OmokSurrender, // 오목 항복했을 때 (currentUser:RoomName)
    OmokUpdatePlayer, // 오목 유저의 상태를 변경 (currentUser:PLAYER or OBSERVER)
    OmokRemoveRoom, // 방 제거될 때 (RoomName)
    OmokUpdateRoom,
    OmokChat, // 오목 채팅 (roomName:sender:message)
    /* 오목 게임 TYPE */
    OmokReady, // 오목 게임 준비 (roomName:sender)
    OmokStart, // 오목 게임 시작 (Object)
    OmokPlaceStone, // 돌 두었을 때 (roomName:sender,Object)
    Turn
}
