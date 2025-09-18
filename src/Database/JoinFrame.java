package Database;
import Database.ZipcodeSearch.ZipcodeSearch;
import Function.ImageResize.ImageResize;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.border.LineBorder;

/* 이미지를 Byte[] 형식으로 변환하기 위해 사용 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/* 그 외 */
// import java.util.Arrays;
import java.util.Objects;

public class JoinFrame extends JFrame {
	/* ID 중복 확인 */
	boolean isIdChecked = false;
	boolean isNicknameChecked = false;

	/* Panel */
	JPanel mainPanel;
	JPanel headerP = new JPanel();
	JPanel mainP = new JPanel();
	JPanel idP = new JPanel();
	JPanel nicknameP = new JPanel();
	JPanel passwdP = new JPanel();
	JPanel confirmPasswdP = new JPanel();
	JPanel nameP = new JPanel();
	JPanel phoneP = new JPanel();
	JPanel birthP = new JPanel();
	JPanel genderP = new JPanel();
	JPanel emailP = new JPanel();
	JPanel submitButtonP = new JPanel();
	JPanel zipcodeP = new JPanel();
	JPanel addressP = new JPanel();
	JPanel detailedAddressP = new JPanel();

	/* Label */
	JLabel titleL = new JLabel("회원가입");
	JLabel nameL = new JLabel("이름");
	JLabel idL = new JLabel("아이디");
	JLabel nicknameL = new JLabel("닉네임");
	JLabel passwdL = new JLabel("비밀번호");
	JLabel confirmPasswdL = new JLabel("비밀번호 확인");
	JLabel phoneL = new JLabel("핸드폰 번호");
	JLabel phoneDashL1 = new JLabel("-");
	JLabel phoneDashL2 = new JLabel("-");
	JLabel birthL = new JLabel("생년월일");
	JLabel birthYearL = new JLabel("년");
	JLabel birthMonthL = new JLabel("월");
	JLabel birthDayL = new JLabel("일");
	JLabel genderL = new JLabel("성별");
	JLabel emailL = new JLabel("이메일");
	JLabel emailL2 = new JLabel("@");
	JLabel zipcodeL = new JLabel("우편번호");
	JLabel addressL = new JLabel("주소");
	JLabel detailedAddressL = new JLabel("상세주소");

	/* TextField */
	JTextField idT = new JTextField();
	JPasswordField passwdT = new JPasswordField();
	JPasswordField confirmPasswdT = new JPasswordField();
	JTextField nameT = new JTextField();
	JTextField nicknameT = new JTextField();
	JTextField phoneMiddleT = new JTextField(4);
	JTextField phoneLastT = new JTextField(4);
	JTextField birthYearT = new JTextField(4);
	JTextField emailT = new JTextField();
	JTextField emailDomainT = new JTextField();
	JTextField zipcodeT = new JTextField();
	JTextField addressT = new JTextField();
	JTextField detailedAddressT = new JTextField();

	/* Combo Box */
	String[] phoneFirstStr = {"010", "011"};
	String[] birthMonthStr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
	String[] birthDayStr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
			"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
	String[] emailDomainStr = {"직접 입력", "naver.com", "gmail.com", "nate.com",
			"yahoo.com", "tistory.com", "daum.net", "hanmail.net"};

	JComboBox<String> phoneFirstComboBox = new JComboBox<>(phoneFirstStr);
	JComboBox<String> birthMonthComboBox = new JComboBox<>(birthMonthStr);
	JComboBox<String> birthDayComboBox = new JComboBox<>(birthDayStr);
	JComboBox<String> emailDomainComboBox = new JComboBox<>(emailDomainStr);

	/* Button */
	JButton checkIdB = new JButton("중복확인");
	JButton checkNicknameB = new JButton("중복확인");
	JRadioButton maleRadioB = new JRadioButton("남자");
	JRadioButton femaleRadioB = new JRadioButton("여자");
	JButton submitB = new JButton("가입하기");
	JButton cancelB = new JButton("가입취소");
	JButton zipcodeSearchB = new JButton("우편번호 찾기");

	/* Image Label */
	JLabel imageL = new JLabel();
	JButton uploadButton = new JButton("사진 첨부");
	ImageIcon defaultImageIcon = new ImageIcon("src/Database/Image/곰.jpg");
	Image defaultImage = defaultImageIcon.getImage();

	ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

	/* 비밀번호 안전성 검사, 비밀번호 확인 */
	JPanel passwdSafetyCheckP = new JPanel();
	JLabel passwdSafetyL = new JLabel("비밀번호를 입력하세요.");
	JProgressBar passwdSafetyProgressBar = new JProgressBar(0, 100);
	JPanel passwdCheckP = new JPanel();
	JLabel passwdCheckL = new JLabel();

	Color buttonColor = new Color(122, 178, 211);

	Database db = new Database();

	JoinFrame() {
		setTitle("회원가입");

		/* MainPanel */
		mainPanel = new JPanel();
		mainPanel.setBounds(100, 100, 480, 645);

		/* 창을 화면 중앙에 표시 */
		setLocationRelativeTo(null);

		/* Panel Size */
		headerP.setBounds(0, 0, 465, 40);
		headerP.setBackground(new Color(153, 204, 255));
		headerP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		mainP.setBounds(0, 40, 465, 570);
		idP.setBounds(30, 10, 300, 33);
		nicknameP.setBounds(30, 45, 300, 33);
		passwdSafetyCheckP.setBounds(30, 80, 300, 33);
		passwdP.setBounds(30, 115, 300, 31);
		confirmPasswdP.setBounds(30, 150, 300, 31);
		passwdCheckP.setBounds(30, 185, 300, 31);
		nameP.setBounds(30, 220, 300, 31);
		phoneP.setBounds(30, 255, 300, 31);
		birthP.setBounds(30, 290, 320, 31);
		genderP.setBounds(30, 325, 300, 31);
		emailP.setBounds(30, 360, 400, 31);
		submitButtonP.setBounds(135, 505, 200, 34);

		/* Zipcode */
		zipcodeP.setBounds(30, 395, 400, 31);
		zipcodeL.setBounds(8, 9, 50, 15);
		zipcodeT.setBounds(90, 5, 110, 21);
		zipcodeSearchB.setBounds(210, 5, 110, 21);
		zipcodeSearchB.setBackground(buttonColor);
		zipcodeSearchB.setForeground(Color.WHITE);
		zipcodeSearchB.setFocusPainted(false);

		addressP.setBounds(30, 430, 400, 31);
		addressL.setBounds(8, 9, 50, 15);
		addressT.setBounds(90, 5, 300, 21);

		detailedAddressP.setBounds(30, 465, 400, 31);
		detailedAddressL.setBounds(8, 9, 60, 15);
		detailedAddressT.setBounds(90, 5, 300, 21);

		/* Label Size, Font */
		titleL.setBounds(185, 10, 92, 26);
		titleL.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		titleL.setForeground(Color.WHITE);
		titleL.setPreferredSize(new Dimension(0, 40));
		idL.setBounds(10, 9, 40, 15);
		nicknameL.setBounds(10, 9, 40, 15);
		passwdSafetyL.setBounds(200, 9, 90, 15);
		passwdSafetyProgressBar.setBounds(90, 9, 100, 15);
		passwdSafetyProgressBar.setValue(0);
		passwdSafetyProgressBar.setStringPainted(true);
		passwdL.setBounds(10, 9, 50, 15);
		confirmPasswdL.setBounds(8, 9, 80, 15);
		passwdCheckL.setBounds(90, 5, 180, 15);
		nameL.setBounds(8, 9, 28, 15);
		phoneL.setBounds(8, 9, 70, 15);
		phoneDashL1.setBounds(155, 6, 16, 15);
		phoneDashL2.setBounds(221, 6, 16, 15);
		birthL.setBounds(8, 9, 52, 15);
		birthYearL.setBounds(144, 9, 16, 15);
		birthMonthL.setBounds(215, 9, 16, 15);
		birthDayL.setBounds(284, 9, 16, 15);
		genderL.setBounds(8, 9, 46, 15);
		emailL.setBounds(8, 9, 46, 15);
		emailL2.setBounds(175, 9, 16, 15);

		/* JTextField Size */
		idT.setBounds(90, 6, 96, 21);
		nicknameT.setBounds(90, 6, 96, 21);
		passwdT.setBounds(90, 5, 130, 21);
		confirmPasswdT.setBounds(90, 5, 130, 21);
		nameT.setBounds(90, 5, 96, 21);
		//birthYearT.setHorizontalAlignment(SwingConstants.LEFT);
		birthYearT.setBounds(90, 5, 50, 21);
		// emailT1.setHorizontalAlignment(SwingConstants.LEFT);
		emailT.setBounds(90, 5, 80, 21);
		emailDomainT.setBounds(192, 5, 80, 21);

		/* Button Size */
		checkIdB.setBounds(205, 5, 85, 23);
		checkIdB.setBackground(buttonColor);
		checkIdB.setForeground(Color.WHITE);
		checkIdB.setFocusPainted(false);

		checkNicknameB.setBounds(205, 5, 85, 23);
		checkNicknameB.setBackground(buttonColor);
		checkNicknameB.setForeground(Color.WHITE);
		checkNicknameB.setFocusPainted(false);

		maleRadioB.setMnemonic('s');
		maleRadioB.setSelected(true);
		maleRadioB.setBounds(90, 5, 60, 23);

		femaleRadioB.setMnemonic('s');
		femaleRadioB.setBounds(150, 5, 60, 23);

		submitB.setBounds(10, 5, 85, 23);
		submitB.setBackground(buttonColor);
		submitB.setForeground(Color.WHITE);
		submitB.setFocusPainted(false);
		cancelB.setBounds(100, 5, 85, 23);
		cancelB.setBackground(buttonColor);
		cancelB.setForeground(Color.WHITE);
		cancelB.setFocusPainted(false);

		/* Image Label Setting */
		imageL.setBorder(new LineBorder(new Color(0, 0, 0), 1));
		imageL.setBounds(340, 10, 100, 120);
		uploadButton.setBounds(340, 130, 100, 23);
		uploadButton.setBackground(buttonColor);
		uploadButton.setForeground(Color.WHITE);
		uploadButton.setFocusPainted(false);
		Image defaultScaleImage = defaultImage.getScaledInstance(imageL.getWidth(), imageL.getHeight(), defaultImage.SCALE_SMOOTH);
		ImageIcon defaultScaledIcon = new ImageIcon(defaultScaleImage);
		imageL.setIcon(defaultScaledIcon);

		setIconImage(gameIcon.getImage());

		/* SetLayout */
		headerP.setLayout(null);
		mainP.setLayout(null);
		idP.setLayout(null);
		nicknameP.setLayout(null);
		passwdSafetyCheckP.setLayout(null);
		passwdP.setLayout(null);
		confirmPasswdP.setLayout(null);
		passwdCheckP.setLayout(null);
		nameP.setLayout(null);
		phoneP.setLayout(null);
		birthP.setLayout(null);
		genderP.setLayout(null);
		emailP.setLayout(null);
		zipcodeP.setLayout(null);
		addressP.setLayout(null);
		detailedAddressP.setLayout(null);

		/* ComboBox Model */
		phoneFirstComboBox.setBounds(90, 5, 55, 21);

		phoneMiddleT.setDropMode(DropMode.INSERT);
		phoneMiddleT.setHorizontalAlignment(SwingConstants.LEFT);
		phoneMiddleT.setBounds(169, 5, 45, 21);

		phoneLastT.setDropMode(DropMode.INSERT);
		phoneLastT.setHorizontalAlignment(SwingConstants.LEFT);
		phoneLastT.setBounds(234, 5, 45, 21);

		birthMonthComboBox.setBounds(162, 5, 48, 21);
		birthDayComboBox.setBounds(233, 5, 48, 21);

		emailDomainComboBox.setBounds(280, 5, 100, 23);

		/* add */
		/* 메인 프레임 Panel ADD */
		mainPanel.setLayout(null);
		setContentPane(mainPanel);

		/* Header Panel */
		mainPanel.add(headerP);
		// mainPanel.getContentPane().add(headerP);
		headerP.add(titleL);

		/* MainPanel */
		mainPanel.add(mainP);
		//mainPanel.getContentPane().add(mainP);

		/* ID Panel */
		mainP.add(idP);
		idP.add(idL); idP.add(idT); idP.add(checkIdB);

		/* Nickname Panel */
		mainP.add(nicknameP);
		nicknameP.add(nicknameL); nicknameP.add(nicknameT); nicknameP.add(checkNicknameB);

		/* Password Safety Panel */
		mainP.add(passwdSafetyCheckP);
		passwdSafetyCheckP.add(passwdSafetyL);
		passwdSafetyCheckP.add(passwdSafetyProgressBar);

		/* Password Panel */
		mainP.add(passwdP); passwdP.add(passwdL); passwdP.add(passwdT);

		/* Confirm Password Panel */
		mainP.add(confirmPasswdP);
		confirmPasswdP.add(confirmPasswdL);
		confirmPasswdP.add(confirmPasswdT);

		/* Password Check Panel */
		mainP.add(passwdCheckP); passwdCheckP.add(passwdCheckL);

		/* Name Panel */
		mainP.add(nameP);
		nameP.add(nameL); nameP.add(nameT);

		/* Phone Number Panel */
		mainP.add(phoneP); phoneP.add(phoneL);
		phoneP.add(phoneFirstComboBox);
		phoneP.add(phoneMiddleT);
		phoneP.add(phoneLastT);
		phoneP.add(phoneDashL1); phoneP.add(phoneDashL2);

		/* Birth Panel */
		mainP.add(birthP); birthP.add(birthL);
		birthP.add(birthYearT);
		birthP.add(birthYearL);
		birthP.add(birthMonthComboBox);
		birthP.add(birthMonthL);
		birthP.add(birthDayComboBox);
		birthP.add(birthDayL);

		/* Gender Panel */
		mainP.add(genderP); genderP.add(genderL);
		genderP.add(maleRadioB); genderP.add(femaleRadioB);

		/* Email Panel */
		mainP.add(emailP); emailP.add(emailL);
		emailP.add(emailT); emailP.add(emailL2);
		emailP.add(emailDomainComboBox);
		emailP.add(emailDomainT);

		/* 회원가입 버튼 패널 */
		mainP.add(submitButtonP);
		submitButtonP.add(submitB); submitButtonP.add(cancelB);

		/* 사진 업로드 */
		mainP.add(uploadButton); mainP.add(imageL);

		/* Zipcode Panel */
		mainP.add(zipcodeP);
		mainP.add(addressP);
		mainP.add(detailedAddressP);
		zipcodeP.add(zipcodeL);
		zipcodeP.add(zipcodeT);
		zipcodeP.add(zipcodeSearchB);
		addressP.add(addressL);
		addressP.add(addressT);
		detailedAddressP.add(detailedAddressL);
		detailedAddressP.add(detailedAddressT);

		/* 버튼 그룹화 */
		ButtonGroup sexRBGroup = new ButtonGroup();
		sexRBGroup.add(femaleRadioB);
		sexRBGroup.add(maleRadioB);

		/* 회원가입 창 크기 */
		setSize(480, 645);
		setLocationRelativeTo(null);
		setResizable(false);

		/* Password Add DocumentListener */
		passwdT.getDocument().addDocumentListener(new PasswordFieldListener());
		confirmPasswdT.getDocument().addDocumentListener(new PasswordFieldListener());

		/* Placeholder */
		String placeholder = "8~16자, 특수문자 포함";
		SwingCompFunc.setPlaceholderPasswordField(passwdT, placeholder);
		SwingCompFunc.setPlaceholderPasswordField(confirmPasswdT, placeholder);

		/* LimitedTextField Event */
		SwingCompFunc.setNumericInputFilter(phoneMiddleT, 4);
		SwingCompFunc.setNumericInputFilter(phoneLastT, 4);
		SwingCompFunc.setNumericInputFilter(birthYearT, 4);
		SwingCompFunc.setNumericInputFilter(zipcodeT, 5);

		SwingCompFunc.setDocumentFilter(passwdT, 16);
		SwingCompFunc.setDocumentFilter(confirmPasswdT, 16);
		SwingCompFunc.setDocumentFilter(idT, 12);
		SwingCompFunc.setDocumentFilter(nicknameT, 12);
		SwingCompFunc.setDocumentFilter(nameT, 12);
		SwingCompFunc.setDocumentFilter(emailT, 12);

		/* Button Event */
		/* checkIdButton Event */
		checkIdB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = idT.getText().trim();
				if (id.isEmpty()) {
					JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else {
					isIdChecked = db.idCheck(id, isIdChecked);
				}
			}
		});

		checkNicknameB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nickname = nicknameT.getText().trim();
				if (nickname.isEmpty()) {
					JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else {
					isNicknameChecked = db.nicknameCheck(nickname, isNicknameChecked);
				}
			}
		});

		/* 아이디 텍스트 필드 변경 확인 */
		idT.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { resetCheck(); }
			@Override
			public void removeUpdate(DocumentEvent e) { resetCheck(); }
			@Override
			public void changedUpdate(DocumentEvent e) { resetCheck(); }
			private void resetCheck() { isIdChecked = false; }
		});

		nicknameT.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { resetCheck(); }
			@Override
			public void removeUpdate(DocumentEvent e) { resetCheck(); }
			@Override
			public void changedUpdate(DocumentEvent e) { resetCheck(); }
			private void resetCheck() { isNicknameChecked = false; }
		});

		/* EmailDomainComboBox Event */
		emailDomainComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedItem = (String) emailDomainComboBox.getSelectedItem();
				if(Objects.requireNonNull(selectedItem).equals("직접 입력")) {
					emailDomainT.setText("");
				} else {
					emailDomainT.setText(selectedItem);
				}
			}
		});

		/* SubmitButton Event */
		submitB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = idT.getText();
				String nickname = nicknameT.getText();
				String passwd = new String(passwdT.getPassword());
				String confirmPasswd = new String(confirmPasswdT.getPassword());
				String name = nameT.getText();
				String middlePhoneNumber = phoneMiddleT.getText();
				String lastPhoneNumber = phoneLastT.getText();
				String phone = phoneFirstComboBox.getSelectedItem() + "-" +
						phoneMiddleT.getText() + "-" + phoneLastT.getText();
				String birthYear = birthYearT.getText();
				String birthMonth = Objects.requireNonNull(birthMonthComboBox.getSelectedItem()).toString();
				String birthDay = Objects.requireNonNull(birthDayComboBox.getSelectedItem()).toString();
				String birth = birthYearT.getText() + "/" +
						birthMonthComboBox.getSelectedItem() + "/" +
						birthDayComboBox.getSelectedItem();
				String firstEmail = emailT.getText();
				String emailDomain = emailDomainT.getText();
				String email = emailT.getText() + "@" +
						emailDomainComboBox.getSelectedItem();
				String gender = maleRadioB.isSelected() ? "남자" : "여자";
				String zipcode = zipcodeT.getText();
				String address = addressT.getText();
				String detailedAddress = detailedAddressT.getText();
				byte[] image = labelImageToByteArray(imageL);

				if (id.isEmpty() || passwd.isEmpty() || name.isEmpty() || confirmPasswd.isEmpty() ||
				middlePhoneNumber.isEmpty() || lastPhoneNumber.isEmpty() ||
				birthYear.isEmpty() || birthMonth.isEmpty() || birthDay.isEmpty() ||
				firstEmail.isEmpty() || emailDomain.isEmpty() ||
				zipcode.isEmpty() || address.isEmpty() || detailedAddress.isEmpty() ) {
					JOptionPane.showMessageDialog(null, "모든 필드를 채워주세요.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else if (!isIdChecked) {
					JOptionPane.showMessageDialog(null, "ID 중복 확인을 하세요.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else if (!isNicknameChecked) {
					JOptionPane.showMessageDialog(null, "닉네임 중복 확인을 하세요.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else if (!SwingCompFunc.isValidPassword(passwd)) {
					JOptionPane.showMessageDialog(null, "비밀번호는 8~16자이고, 영문, 숫자, 특수문자를 포함해야 합니다.");
				} else if (!passwd.equals(confirmPasswd)){
					JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.",
							"오류", JOptionPane.ERROR_MESSAGE);
				} else {
					if (db.joinCheck(id, nickname, passwd, name, phone, birth, gender, email, image, zipcode, address, detailedAddress)) {
						JOptionPane.showMessageDialog(null, "회원가입 완료!",
								"정보", JOptionPane.INFORMATION_MESSAGE);
						initField();
						imageL.setIcon(defaultScaledIcon);
						dispose();
					} else {
						JOptionPane.showMessageDialog(null, "회원가입에 실패하였습니다");
						initField();
						imageL.setIcon(defaultScaledIcon);
					}
				}
			}
		});

		/* cancelButton Event */
		cancelB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initField();
				imageL.setIcon(defaultScaledIcon);
				dispose();
			}
		});

		/* zipcodeSearchButton Event */
		zipcodeSearchB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZipcodeSearch zipcode = new ZipcodeSearch(zipcodeT, addressT);
				zipcode.setVisible(true);
			}
		});

		/* uploadButton Event */
		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                try {
                    openImageSelection();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
		});
	}

	/* 함수 */
	/* "JLabel"에서 이미지 아이콘을 가져와 byte[]로 변환*/
	public static byte[] labelImageToByteArray(JLabel label) {
		/* 라벨에 있는 "ImageIcon" 객체 가져오기 */
		ImageIcon icon = (ImageIcon) label.getIcon();
		if (icon == null) {
			return null;
		}

		/* ImageIcon 이미지 추출*/
		Image image = icon.getImage();

		/* 이미지를 "BufferedImage"로 변환 */
		BufferedImage bufferedImage = new BufferedImage(
				image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB
		);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);

		/* "ByteArrayOutputStream"을 사용해 이미지를 byte[]로 변환 */
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			/* JPG 또는 PNG 포맷으로 저장 가능 */
			ImageIO.write(bufferedImage, "jpg", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* 비밀번호 확인 리스너 */
	public class PasswordFieldListener implements  DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			checkPasswords();
			PasswordSafetyStrength();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkPasswords();
			PasswordSafetyStrength();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkPasswords();
			PasswordSafetyStrength();
		}
	}

	/* Password Check */
	public void checkPasswords() {
		String password = new String(passwdT.getPassword());
		String confirmPassword = new String(confirmPasswdT.getPassword());
		String placeholder = "8~16자, 특수문자 포함";

		if (password.equals(placeholder) || confirmPassword.equals(placeholder)) {
			passwdCheckL.setText("비밀번호를 입력하세요.");
			passwdCheckL.setForeground(Color.GRAY);
		} else if(password.equals(confirmPassword)) {
			passwdCheckL.setText("비밀번호가 일치합니다.");
			passwdCheckL.setForeground(new Color(47, 157, 39));
		} else {
			passwdCheckL.setText("비밀번호가 일치하지 않습니다.");
			passwdCheckL.setForeground(Color.RED);
		}
	}

	public void PasswordSafetyStrength() {
		String password = new String(passwdT.getPassword());
		int strength = SwingCompFunc.calculateStrength(password);
		String placeholder = "8~16자, 특수문자 포함";

		if (password.equals(placeholder) || password.isEmpty()) {
			passwdSafetyL.setText("안전성 검사");
			passwdSafetyL.setForeground(Color.GRAY);
		}else if (strength <= 20) {
			passwdSafetyL.setText("위험");
			passwdSafetyL.setForeground(Color.RED);
		} else if (strength <= 40) {
			passwdSafetyL.setText("약함");
			passwdSafetyL.setForeground(Color.RED);
		} else if (strength <=60) {
			passwdSafetyL.setText("보통");
			passwdSafetyL.setForeground(Color.ORANGE);
		} else {
			passwdSafetyL.setText("정상");
			passwdSafetyL.setForeground(new Color(47, 157, 39));
		}

		passwdSafetyProgressBar.setValue(strength);
	}

	public int calculateStrength(String password) {
		int strength = 0;
		String placeholder = "8~16자, 특수문자 포함";

		if (password.length() >= 8 & !password.equals(placeholder)) strength += 20;
		if (password.matches(".*\\d.*") & !password.equals(placeholder)) strength += 20; // 숫자가 포함되어 있으면
		if (password.matches(".*[a-z].*") & !password.equals(placeholder)) strength += 20; // 소문자가 포함되어 있으면
		if (password.matches(".*[A-Z].*") & !password.equals(placeholder)) strength += 20; // 대문자가 포함되어 있으면
		if (password.matches(".*[!@#\\$%^&*].*") & !password.equals(placeholder)) strength += 20; // 특수 문자가 포함되어 있으면

		return strength;
	}

	/* 필드 초기화 */
	public void initField() {
		idT.setText("");
		nameT.setText("");
		nicknameT.setText("");
		phoneMiddleT.setText(""); phoneLastT.setText("");
		birthYearT.setText("");
		birthMonthComboBox.setSelectedItem("1");
		birthDayComboBox.setSelectedItem("1");
		maleRadioB.setSelected(true);
		emailT.setText(""); emailDomainT.setText("");
		zipcodeT.setText(""); addressT.setText("");
		detailedAddressT.setText("");

		String placeholder = "8~16자, 특수문자 포함";
		passwdT.setText(placeholder);
		passwdT.setEchoChar((char) 0);
		passwdT.setForeground(Color.GRAY);

		confirmPasswdT.setText(placeholder);
		confirmPasswdT.setEchoChar((char) 0);
		confirmPasswdT.setForeground(Color.GRAY);
	}

	/* 숫자 문자열인지 확인 */
	public boolean isNumeric(String text) {
		/* 정규 표현식을 사용 (\d+, 하나 이상의 숫자) */
		return text.matches("\\d+");
	}

	/* 입력 제한 설정 */
	public void setDocumentFilter(JTextComponent textComponent, int maxLength) {
		((AbstractDocument) textComponent.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override // 새로운 문자열을 삽입할 때
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if ((fb.getDocument().getLength() + string.length()) <= maxLength) {
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override // 텍스트 일부를 대체할 때
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				if ((fb.getDocument().getLength() + text.length() - length) <= maxLength) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});
	}

	/* 숫자만 입력 제한 설정 */
	public void setNumericInputFilter(JTextComponent textComponent, int maxLength) {
		((AbstractDocument) textComponent.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override // 새로운 문자열을 삽입할 때
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (string.isEmpty() || isNumeric(string) && (fb.getDocument().getLength() + string.length() <= maxLength)) {
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override // 텍스트 일부를 대체할 때
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				if (text.isEmpty() || isNumeric(text) && (fb.getDocument().getLength() + text.length() - length <= maxLength)) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});
	}

	/* 자른 이미지 가져오기 */
	public void setCroppedImage(BufferedImage croppedImage) {
		if (croppedImage != null) {
			Image scaledImage = croppedImage.getScaledInstance(imageL.getWidth(), imageL.getHeight(), Image.SCALE_SMOOTH);
			imageL.setIcon(new ImageIcon(scaledImage));
		}
	}

	/* 이미지 선택 창 열기 */
	private void openImageSelection() throws IOException {
		/* 파일 선택 창 생성 (파일만 선택 가능) */
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		/* 파일 필터 설정 (이미지 파일만 선택 가능) */
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
				"Image Files", "jpg", "png", "jpeg", "gif"
		));

		/* 파일 선택 창을 띄우고 값을 "result"에 저장 */
		int result = fileChooser.showOpenDialog(null);
		/* 파일 선택 후 확인을 눌렀을 경우 */
		if (result == JFileChooser.APPROVE_OPTION) {
			/* 선택한 파일을 "selectedFile"에 저장 */
			File selectedFile = fileChooser.getSelectedFile();
			ImageResize imageResize = new ImageResize(this, selectedFile);
			imageResize.setVisible(true);

			/* 선택된 파일을 이미지로 로드 */
			ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());

			/* 이미지 크기를 라벨에 맞게 조정 */
			Image image = imageIcon.getImage();
			/* 이미지를 "JLabel"의 크기에 맞게 조정 */
			/* "SCALE_SMOOTH" 옵션으로 이미지를 부드럽게 크기 조정 */
			Image resizedImage = image.getScaledInstance(
					imageL.getWidth(), imageL.getHeight(), Image.SCALE_SMOOTH
			);
			/* "ImageL"에 크기 조정된 이미지를 표시 */
			imageL.setIcon(new ImageIcon(resizedImage));
		}
	}
}