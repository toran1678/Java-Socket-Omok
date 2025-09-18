package TestPack.Window;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

public class userEdit {

	private JFrame frame;
	private JTextField nicknameTextField;
	private JTextField birthYearT;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField passwordT;
	private JTextField phoneMiddleT;
	private JTextField phoneLastT;
	private JTextField emailT;
	private JTextField emailDomainT;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					userEdit window = new userEdit();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public userEdit() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 490);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		
		JLabel userEditLabel = new JLabel("회원 정보 수정");
		userEditLabel.setFont(new Font("굴림", Font.BOLD, 20));
		topPanel.add(userEditLabel);
		
		JPanel bottomPanel = new JPanel();
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		JButton editButton = new JButton("수정하기");
		editButton.setFont(new Font("굴림", Font.BOLD, 15));
		bottomPanel.add(editButton);
		
		JButton btnNewButton = new JButton("취소하기");
		btnNewButton.setFont(new Font("굴림", Font.BOLD, 15));
		bottomPanel.add(btnNewButton);
		
		JPanel middlePanel = new JPanel();
		frame.getContentPane().add(middlePanel, BorderLayout.CENTER);
		middlePanel.setLayout(null);
		
		JPanel idPanel = new JPanel();
		idPanel.setBounds(30, 10, 300, 33);
		middlePanel.add(idPanel);
		idPanel.setLayout(null);
		
		JLabel idLabel = new JLabel("아이디");
		idLabel.setBounds(10, 9, 40, 15);
		idPanel.add(idLabel);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(90, 6, 120, 21);
		idPanel.add(textField_1);
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(null);
		namePanel.setBounds(30, 40, 300, 33);
		middlePanel.add(namePanel);
		
		JLabel nameLabel = new JLabel("이름");
		nameLabel.setBounds(10, 9, 40, 15);
		namePanel.add(nameLabel);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(90, 6, 120, 21);
		namePanel.add(textField);
		
		JPanel nicknamePanel = new JPanel();
		nicknamePanel.setLayout(null);
		nicknamePanel.setBounds(30, 70, 300, 33);
		middlePanel.add(nicknamePanel);
		
		JLabel nicknameLabel = new JLabel("닉네임");
		nicknameLabel.setBounds(10, 9, 40, 15);
		nicknamePanel.add(nicknameLabel);
		
		nicknameTextField = new JTextField();
		nicknameTextField.setBounds(90, 6, 120, 21);
		nicknamePanel.add(nicknameTextField);
		nicknameTextField.setColumns(10);
		
		JPanel phonePanel = new JPanel();
		phonePanel.setLayout(null);
		phonePanel.setBounds(30, 130, 300, 33);
		middlePanel.add(phonePanel);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout(null);
		passwordPanel.setBounds(30, 100, 300, 33);
		middlePanel.add(passwordPanel);
		
		JLabel passwordL = new JLabel("비밀번호");
		passwordL.setBounds(10, 9, 80, 15);
		passwordPanel.add(passwordL);
		
		passwordT = new JTextField();
		passwordT.setColumns(10);
		passwordT.setBounds(90, 6, 120, 21);
		passwordPanel.add(passwordT);
		
		JLabel phoneNumberL = new JLabel("핸드폰 번호");
		phoneNumberL.setBounds(10, 9, 80, 15);
		phonePanel.add(phoneNumberL);
		
		JComboBox phoneFirstComboBox = new JComboBox();
		phoneFirstComboBox.setBounds(90, 5, 55, 21);
		phonePanel.add(phoneFirstComboBox);
		
		JLabel phoneDashLabel1 = new JLabel("-");
		phoneDashLabel1.setBounds(155, 6, 16, 15);
		phonePanel.add(phoneDashLabel1);
		
		JLabel phoneDashLabel2 = new JLabel("-");
		phoneDashLabel2.setBounds(221, 6, 16, 15);
		phonePanel.add(phoneDashLabel2);
		
		phoneMiddleT = new JTextField();
		phoneMiddleT.setColumns(10);
		phoneMiddleT.setBounds(169, 5, 45, 21);
		phonePanel.add(phoneMiddleT);
		
		phoneLastT = new JTextField();
		phoneLastT.setColumns(10);
		phoneLastT.setBounds(234, 5, 45, 21);
		phonePanel.add(phoneLastT);
		
		JPanel birthPanel = new JPanel();
		birthPanel.setLayout(null);
		birthPanel.setBounds(30, 160, 300, 33);
		middlePanel.add(birthPanel);
		
		birthYearT = new JTextField();
		birthYearT.setColumns(10);
		birthYearT.setBounds(90, 5, 50, 21);
		birthPanel.add(birthYearT);
		
		JLabel birthL = new JLabel("생일");
		birthL.setBounds(10, 9, 80, 15);
		birthPanel.add(birthL);
		
		JLabel birthYearL = new JLabel("년");
		birthYearL.setBounds(144, 9, 16, 15);
		birthPanel.add(birthYearL);
		
		JLabel birthMonthL = new JLabel("월");
		birthMonthL.setBounds(215, 9, 16, 15);
		birthPanel.add(birthMonthL);
		
		JLabel birthDayL = new JLabel("일");
		birthDayL.setBounds(284, 9, 16, 15);
		birthPanel.add(birthDayL);
		
		JComboBox birthMonthComboBox = new JComboBox();
		birthMonthComboBox.setBounds(162, 5, 48, 21);
		birthPanel.add(birthMonthComboBox);
		
		JComboBox birthDayComboBox = new JComboBox();
		birthDayComboBox.setBounds(233, 5, 48, 21);
		birthPanel.add(birthDayComboBox);
		
		JPanel genderPanel = new JPanel();
		genderPanel.setLayout(null);
		genderPanel.setBounds(30, 190, 300, 33);
		middlePanel.add(genderPanel);
		
		JLabel genderL = new JLabel("성별");
		genderL.setBounds(10, 9, 80, 15);
		genderPanel.add(genderL);
		
		JRadioButton mailRadioB = new JRadioButton("남자");
		mailRadioB.setBounds(90, 5, 60, 23);
		genderPanel.add(mailRadioB);
		
		JRadioButton femaleRadioB = new JRadioButton("여자");
		femaleRadioB.setBounds(150, 5, 60, 23);
		genderPanel.add(femaleRadioB);
		
		JPanel profileCharacterPanel = new JPanel();
		profileCharacterPanel.setLayout(null);
		profileCharacterPanel.setBounds(30, 250, 300, 110);
		middlePanel.add(profileCharacterPanel);
		
		JLabel profileCharacterLabel = new JLabel("이미지");
		profileCharacterLabel.setBounds(50, 10, 80, 90);
		profileCharacterPanel.add(profileCharacterLabel);
		
		JButton selectCharacterButton = new JButton("캐릭터 선택");
		selectCharacterButton.setBounds(150, 35, 125, 40);
		profileCharacterPanel.add(selectCharacterButton);
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(null);
		emailPanel.setBounds(30, 220, 400, 33);
		middlePanel.add(emailPanel);
		
		JLabel emailL = new JLabel("이메일");
		emailL.setBounds(10, 9, 80, 15);
		emailPanel.add(emailL);
		
		emailT = new JTextField();
		emailT.setColumns(10);
		emailT.setBounds(90, 5, 80, 21);
		emailPanel.add(emailT);
		
		JComboBox emailDomainComboBox = new JComboBox();
		emailDomainComboBox.setBounds(280, 5, 100, 23);
		emailPanel.add(emailDomainComboBox);
		
		JLabel emailL2 = new JLabel("@");
		emailL2.setBounds(175, 9, 16, 15);
		emailPanel.add(emailL2);
		
		emailDomainT = new JTextField();
		emailDomainT.setColumns(10);
		emailDomainT.setBounds(192, 5, 80, 21);
		emailPanel.add(emailDomainT);
	}
}
