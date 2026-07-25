package com.icc.eserviceshelper.utils

object ContentProvider {

    fun getTitle(type: String): String {
        return when (type) {
            "ABOUT" -> "About App"
            "PRIVACY" -> "Privacy Policy"
            "TERMS" -> "Terms & Conditions"
            "DISCLAIMER" -> "Disclaimer"
            "SOURCES" -> "Sources & References"
            "CONTACT" -> "Contact Us"
            "REPORT" -> "Report Issue"
            "SHARE" -> "Share App"
            "RATE" -> "Rate App"
            "UPDATE" -> "Check for Updates"
            "VERSION" -> "Version"
            "DEVELOPER" -> "Developer"
            "ORGANIZATION" -> "Organization"
            "INFO" -> "App Information"
            else -> "Information"
        }
    }

    fun getContent(type: String): String {
        return when (type) {
            "ABOUT" -> getAboutContent()
            "PRIVACY" -> getPrivacyContent()
            "TERMS" -> getTermsContent()
            "DISCLAIMER" -> getDisclaimerContent()
            "SOURCES" -> getSourcesContent()
            "CONTACT" -> getContactContent()
            "REPORT" -> getReportContent()
            "SHARE" -> getShareContent()
            "RATE" -> getRateContent()
            "UPDATE" -> getUpdateContent()
            "VERSION" -> getAppInfoContent()
            "DEVELOPER" -> getAppInfoContent()
            "ORGANIZATION" -> getAppInfoContent()
            "INFO" -> getAppInfoContent()
            else -> "Content not available."
        }
    }

    private fun getAboutContent(): String {
        return """
WELCOME TO ESERVICES ICC HELPER

Purpose of the Application
eServices ICC Helper is designed to be your ultimate digital companion for navigating the complex landscape of government and digital services in India. Our mission is to simplify the process of accessing essential services, providing users with clear, concise, and accurate information at their fingertips.

Why this Application was Created
In an era of rapid digitalization, many citizens find it challenging to keep track of the ever-changing procedures, required documents, and official portals for various government schemes and services. We identified a gap in accessible, easy-to-understand guidance and decided to create a centralized hub that empowers users with knowledge and saves their valuable time.

Key Features
- Step-by-step application guides for major services.
- Detailed lists of required documents for each service.
- Direct links to official government portals.
- Eligibility criteria checker and information.
- Frequently Asked Questions (FAQs) to resolve common queries.
- Downloadable PDF resources for offline reference.
- Clean, intuitive, and modern user interface.

Supported Services
We currently provide comprehensive information for a wide range of services, including:
- Aadhaar Services (Enrollment, Updates, PVC Card)
- PAN Card Services (New Application, Corrections, Linking)
- Voter ID Services (Registration, E-Epic Download, Corrections)
- Passport Seva (Appointment Booking, Document Requirements)
- Driving License (Learning License, Permanent DL, Renewals)
- Ration Card Services (Application, Name Addition, State Portals)
- Ayushman Bharat (Golden Card, Eligibility, Hospital Search)

Benefits for Users
- Time-Saving: Find all information in one place without searching multiple websites.
- Accuracy: We regularly update our content based on official government notifications.
- Ease of Use: Content is written in simple, jargon-free English for better understanding.
- Accessibility: Access vital information anytime, anywhere, even without a high-speed connection.

How the Application Works
Simply browse through the categories on the home screen, select the service you are interested in, and follow the detailed guide provided. Each guide is structured to lead you from the initial requirements to the final application stage, ensuring a smooth experience.

Future Improvements
We are committed to continuous growth. Future updates will include:
- Multi-language support (Hindi and other regional languages).
- More localized state-government services.
- Real-time status tracking for certain services.
- Enhanced interactive guides.

Closing Message
Thank you for choosing eServices ICC Helper. We are dedicated to making digital India accessible to everyone. Your journey towards hassle-free service access starts here.

eServices ICC Helper Team
        """.trimIndent()
    }

    private fun getPrivacyContent(): String {
        return """
PRIVACY POLICY

Introduction
eServices ICC Helper ("we," "us," or "our") respects your privacy and is committed to protecting it through our compliance with this policy. This policy describes the types of information we may collect from you or that you may provide when you use the eServices ICC Helper mobile application.

Information We Collect
We want to clarify that eServices ICC Helper is primarily an informational application. 
- Usage Data: We may collect information about how you interact with the app, such as the features you use and the time spent on the app.
- Device Information: We may collect information about your mobile device, including the hardware model, operating system version, and unique device identifiers.

Information We Do Not Collect
- Personal Identity Information: We do not collect your name, address, social security number, Aadhaar number, PAN number, or any other sensitive personal identifiers.
- Financial Information: We do not collect credit card details or bank account information.
- Location Data: We do not track your precise GPS location.

Permissions Used
- Internet: Required to fetch the latest information, guides, and links from our servers.
- Access Network State: Used to check if the device is connected to the internet.
- Read/Write External Storage: Required if you choose to download PDF guides to your device.

Third Party Services
The app may contain links to third-party government websites. Please be aware that these websites have their own privacy policies. We do not have control over and are not responsible for the privacy practices of these third parties. We also use standard Google Play Services for app functionality and analytics.

How Information is Used
The information we collect is used solely to:
- Improve the functionality and user experience of the application.
- Analyze usage patterns to provide better content.
- Fix bugs and technical issues.

Data Protection
We implement a variety of security measures to maintain the safety of your device information. However, no method of transmission over the internet or method of electronic storage is 100% secure.

Children's Privacy
Our application does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13.

Changes to this Policy
We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the "Last Updated" date.

Contact Information
If you have any questions about this Privacy Policy, please contact us:
Email: icc@indiacybercafe.com
Phone: +91 9203251821
        """.trimIndent()
    }

    private fun getTermsContent(): String {
        return """
TERMS & CONDITIONS

Acceptance of Terms
By downloading and using eServices ICC Helper, you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please do not use the application.

Purpose of the Application
eServices ICC Helper is an educational and informational tool. It provides guides, document lists, and links related to various digital and government services.

User Responsibilities
- You agree to use the app only for lawful purposes.
- You are responsible for ensuring the accuracy of any information you provide on third-party government websites linked through this app.
- You must not attempt to disrupt the app's functionality or security.

Content Usage
The content provided in this app is for personal, non-commercial use only. You may not reproduce, distribute, or create derivative works from this content without our express permission.

Intellectual Property
All trademarks, logos, and content within the app are the property of eServices ICC Helper or their respective owners.

Limitation of Liability
eServices ICC Helper is not liable for any direct, indirect, incidental, or consequential damages arising out of your use of the application. We do not guarantee that the application will be error-free or uninterrupted.

Accuracy of Information
While we strive to provide accurate and up-to-date information, the procedures for government services can change without notice. Users are advised to verify all information from official sources.

External Links
The app contains links to external websites that are not operated by us. We have no control over the content and practices of these sites and cannot accept responsibility for their respective privacy policies or terms of use.

Updates
We may update the application and these Terms and Conditions from time to time to reflect changes in our services or legal requirements.

Termination
We reserve the right to terminate or suspend access to our application immediately, without prior notice or liability, for any reason whatsoever.

Contact Information
For any queries regarding these terms, please contact us:
Email: icc@indiacybercafe.com
Phone: +91 9203251821
        """.trimIndent()
    }

    private fun getDisclaimerContent(): String {
        return """
DISCLAIMER

Official Status
eServices ICC Helper is an independent, private application. It is NOT affiliated with, associated with, endorsed by, or in any way officially connected to any government department, agency, or entity.

Non-Representation
This application does NOT represent any government entity. The creators of this app are not government officials and do not have the authority to process government applications or provide official government documents.

Purpose of Information
The information provided within this application is for educational and informational purposes ONLY. It is intended to help users understand the general procedures and requirements for various services.

Verification Required
Users should ALWAYS verify the information provided in this app by visiting the official government websites or contacting the relevant government departments directly. Do not rely solely on the information provided here for making critical decisions.

No Guarantee of Service
Use of this app or following the guides provided herein does not guarantee the successful processing or approval of any government service, application, or benefit. Approvals are subject to the rules and regulations of the respective government authorities.

Liability
eServices ICC Helper and its developers take no legal responsibility for any decisions made by the user based on the information provided in the app. Any reliance you place on such information is strictly at your own risk.

Data Accuracy
While we make every effort to keep the information up-to-date and correct, we make no representations or warranties of any kind, express or implied, about the completeness, accuracy, reliability, suitability, or availability with respect to the application or the information contained on the application for any purpose.
        """.trimIndent()
    }

    private fun getSourcesContent(): String {
        return """
SOURCES & REFERENCES

Official Information Sources
eServices ICC Helper relies on information from official government portals to provide accurate guides. Below are the primary sources we reference:

UIDAI (Aadhaar)
Official Website: https://uidai.gov.in
Source for: Aadhaar enrollment, updates, and PVC card information.

Income Tax Department
Official Website: https://www.incometax.gov.in
Source for: Income tax filing, tax services, and informational resources.

PAN Services (Protean)
Official Website: https://tinpan.proteantech.in/services/pan/pan-index.html
Source for: Permanent Account Number (PAN) applications and corrections.

Online PAN Application
Official Website: https://onlineservices.proteantech.in/paam/endUserRegisterContact.html
Source for: Online registration and application for new PAN cards.

PAN Status Tracking
Official Website: https://tin.tin.proteantech.in/pantan/StatusTrack.html
Source for: Checking the real-time status of PAN card applications.

Election Commission of India (Voter ID)
Official Website: https://voters.eci.gov.in
Source for: Voter registration, EPIC downloads, and electoral roll information.

Passport Seva
Official Website: https://passportindia.gov.in
Source for: Passport applications, appointment scheduling, and document requirements.

Parivahan Sewa (Driving License)
Official Website: https://parivahan.gov.in
Source for: Learning licenses, driving licenses, and vehicle-related services.

Ayushman Bharat (PM-JAY)
Official Website: https://beneficiary.nha.gov.in
Source for: Health insurance benefits, beneficiary identification, and hospital search.

National Food Security Portal (Ration Card)
Official Website: https://nfsa.gov.in
Source for: Ration card schemes and state-wise portal links.

DigiLocker
Official Website: https://www.digilocker.gov.in
Source for: Digital document storage and issuance information.

UMANG
Official Website: https://web.umang.gov.in
Source for: Unified mobile application for various government services.

MyScheme
Official Website: https://www.myscheme.gov.in
Source for: Information on various central and state government schemes.

National Portal of India
Official Website: https://www.india.gov.in
Source for: Centralized access to various government information and services.

Important Note:
Users are strongly encouraged to visit these official portals directly for the most current information, to submit applications, and to track their status. eServices ICC Helper is not responsible for the content or availability of these external sites.
        """.trimIndent()
    }

    private fun getContactContent(): String {
        return """
CONTACT US

Customer Support
We are here to help you. If you have any questions, concerns, or need assistance with the app, please feel free to reach out to us through the following channels:

Support Email
[icc@indiacybercafe.com](mailto:icc@indiacybercafe.com)
We aim to respond to all email inquiries within 24-48 business hours.

Support Phone
+91 9203251821
Available for calls and WhatsApp support.

Office Hours
Monday to Saturday: 10:00 AM – 7:00 PM (IST)
Sunday: Closed

Feedback & Suggestions
Your feedback helps us improve. If you have suggestions for new features or improvements to our existing guides, please let us know. We value user input and strive to make eServices ICC Helper better with every update.

Business Enquiries
For business-related queries or partnerships, please contact us via our support email with the subject line "Business Enquiry".

Technical Support
Encountering a bug or a crash? Please provide details about your device model and the steps that led to the issue when contacting technical support.

Response Time
While we strive for quick responses, please allow us some time during holidays and peak periods. We appreciate your patience.

Contact Details Summary:
Organization: eServices ICC Helper
Email: icc@indiacybercafe.com
Phone: +91 9203251821
        """.trimIndent()
    }

    private fun getReportContent(): String {
        return """
REPORT AN ISSUE

How to Report
If you encounter any problems while using eServices ICC Helper, we want to know so we can fix them as soon as possible. You can report various types of issues:

Wrong Information
If you find that a guide contains outdated or incorrect information, please let us know the service name and the specific section that needs correction.

Broken PDF Links
Are you unable to download a guide? Please report which PDF link is not working so we can update the source.

Application Crashes
In case the app closes unexpectedly, please describe what you were doing at the time of the crash. Mentioning your phone model and Android version is very helpful.

Feature Requests
Have an idea for a new service guide or a tool that would be useful? We are always looking for ways to expand our app's utility.

Content Corrections
Spelling mistakes or formatting issues? Help us keep the app professional by pointing them out.

Details to Include
When reporting an issue via email (icc@indiacybercafe.com), please include:
1. Type of issue (e.g., Crash, Content Error).
2. Screen name where the issue occurred.
3. Description of the problem.
4. (Optional) Screenshot of the issue.
5. Your device model and OS version.

Our Commitment
We take every report seriously and work diligently to resolve issues in a timely manner. Thank you for helping us maintain the quality and reliability of eServices ICC Helper.
        """.trimIndent()
    }

    private fun getShareContent(): String {
        return """
SHARE APP

Why Sharing Helps
eServices ICC Helper was created to empower citizens with information. By sharing this app, you help your friends, family, and community access essential digital services with ease. Many people struggle with government procedures; your share could save someone hours of confusion.

Benefits to Friends and Family
- Easy access to Aadhaar, PAN, and Voter ID guides.
- Clear lists of required documents.
- Direct official links.
- Verified and updated information.

Call-to-Action
Help us reach more people! Click the share button below to send a download link to your contacts on WhatsApp, Telegram, or via Email.

Promotional Message
"Simplify your access to government services with eServices ICC Helper! Get step-by-step guides for Aadhaar, PAN, Voter ID, Passport, and more. Download the app today and stay informed!"
        """.trimIndent()
    }

    private fun getRateContent(): String {
        return """
RATE APP

Why Ratings Matter
Ratings and reviews are the lifeblood of our application. They help other users discover eServices ICC Helper and provide us with the motivation to keep improving and adding new features.

Benefits of Feedback
- Helps us identify what you love about the app.
- Highlights areas where we can improve.
- Increases the app's visibility in the Play Store.

Appreciation Message
We sincerely appreciate the time you take to rate us. Whether it's a five-star review or constructive criticism, we listen to every user.

Five-Star Encouragement
If you find eServices ICC Helper helpful, please consider giving us a 5-star rating on the Google Play Store. It only takes a minute but makes a huge difference for us!

Call-to-Action
Ready to share your experience? Click the 'Rate Now' button to go to the Play Store.
        """.trimIndent()
    }

    private fun getUpdateContent(): String {
        return """
CHECK FOR UPDATES

Importance of Updates
Staying updated ensures you have the best experience with eServices ICC Helper. We regularly release updates to enhance the app's performance and accuracy.

What You Receive in Updates
- New Service Guides: We frequently add information for more government and digital services.
- Accuracy Improvements: We update existing guides to reflect the latest government rules and portal changes.
- Performance Enhancements: Faster loading times and smoother navigation.
- Security Improvements: Ensuring your app remains safe to use.
- Bug Fixes: Resolving any issues reported by our users.

How to Update
You can check for the latest version of eServices ICC Helper directly on the Google Play Store. We recommend enabling 'Auto-update' for the best experience.

Friendly Message
"We are always working behind the scenes to make eServices ICC Helper better for you. Check back often to ensure you are using the most recent version!"
        """.trimIndent()
    }

    private fun getAppInfoContent(): String {
        return """
APP INFORMATION

Application Name
eServices ICC Helper

Application Description
A comprehensive informational guide for various digital and government services in India, including Aadhaar, PAN, Voter ID, and more.

Current Version
1.1.1

Organization
eServices ICC Helper

Support Email
icc@indiacybercafe.com

Support Phone
+91 9203251821

Platform
Android

Category
Education / Informational

Target Audience
Indian Citizens

Release Information
Initial Release: July 2026

Copyright Notice
© 2026 eServices ICC Helper. All Rights Reserved.

License
Proprietary. For personal use only.

Official Contact
For official inquiries, please use the provided email or phone number.
        """.trimIndent()
    }
}