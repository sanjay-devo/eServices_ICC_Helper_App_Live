package com.icc.eserviceshelper.utils

import com.icc.eserviceshelper.R
import com.icc.eserviceshelper.models.InfoItem

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

    fun getInfoItems(type: String): List<InfoItem> {
        return when (type) {
            "ABOUT" -> getAboutItems()
            "PRIVACY" -> getPrivacyItems()
            "TERMS" -> getTermsItems()
            "DISCLAIMER" -> getDisclaimerItems()
            "SOURCES" -> getSourcesItems()
            "CONTACT" -> getContactItems()
            "VERSION" -> getAppInfoItems()
            "DEVELOPER" -> getDeveloperItems()
            "ORGANIZATION" -> getOrganizationItems()
            "UPDATE" -> getUpdateItems()
            else -> emptyList()
        }
    }

    private fun getAboutItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "WELCOME TO ESERVICES ICC HELPER",
            "eServices ICC Helper is designed to be your ultimate digital companion for navigating the complex landscape of government and digital services in India.",
            R.drawable.ic_info
        ),
        InfoItem.Section(
            "Purpose & Mission",
            "Our mission is to simplify the process of accessing essential services, providing users with clear, concise, and accurate information at their fingertips.",
            R.drawable.ic_public
        ),
        InfoItem.Section(
            "Why this Application was Created",
            "In an era of rapid digitalization, many citizens find it challenging to keep track of the ever-changing procedures. We identified a gap in accessible, easy-to-understand guidance and created this centralized hub.",
            R.drawable.ic_description
        ),
        InfoItem.Section(
            "Key Features",
            "• Step-by-step application guides\n• Detailed lists of required documents\n• Direct links to official portals\n• Eligibility criteria information\n• Frequently Asked Questions (FAQs)\n• Downloadable PDF resources",
            R.drawable.ic_star_rate
        ),
        InfoItem.Section(
            "Supported Services",
            "• Aadhaar Services\n• PAN Card Services\n• Voter ID Services\n• Passport Seva\n• Driving License\n• Ration Card Services\n• Ayushman Bharat",
            R.drawable.ic_public
        ),
        InfoItem.Section(
            "Future Improvements",
            "We are committed to multi-language support (Hindi and regional), more localized state services, and enhanced interactive guides.",
            R.drawable.ic_system_update
        )
    )

    private fun getPrivacyItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "Introduction",
            "eServices ICC Helper respects your privacy. This policy describes the types of information we may collect from you or that you may provide.",
            R.drawable.ic_privacy_tip
        ),
        InfoItem.Section(
            "Information We Collect",
            "We collect minimal Usage Data and Device Information to improve functionality. No personal or sensitive data is collected.",
            R.drawable.ic_info
        ),
        InfoItem.Section(
            "What We Do NOT Collect",
            "• Personal Identity (Aadhaar, PAN, Name)\n• Financial Information\n• Precise Location Data",
            R.drawable.ic_warning,
            isWarning = true
        ),
        InfoItem.Section(
            "Permissions Used",
            "• Internet: To fetch latest guides\n• Network State: To check connectivity\n• Storage: To download PDF guides",
            R.drawable.ic_description
        ),
        InfoItem.Section(
            "Third Party Services",
            "The app contains links to official government websites. These sites have their own privacy policies which we do not control.",
            R.drawable.ic_public
        ),
        InfoItem.Section(
            "Contact for Privacy",
            "Email: icc@indiacybercafe.com\nPhone: +91 9203251821",
            R.drawable.ic_call
        )
    )

    private fun getTermsItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "Acceptance of Terms",
            "By downloading and using eServices ICC Helper, you agree to be bound by these Terms and Conditions.",
            R.drawable.ic_description
        ),
        InfoItem.Section(
            "User Responsibilities",
            "You agree to use the app for lawful purposes and are responsible for verifying information on official government websites.",
            R.drawable.ic_person
        ),
        InfoItem.Section(
            "Limitation of Liability",
            "eServices ICC Helper is not liable for any damages arising out of your use of the application. The app is an informational tool only.",
            R.drawable.ic_warning,
            isWarning = true
        ),
        InfoItem.Section(
            "Accuracy of Information",
            "Procedures can change without notice. Users are advised to verify all information from official sources provided in the app.",
            R.drawable.ic_info
        )
    )

    private fun getDisclaimerItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "Official Status",
            "eServices ICC Helper is an independent, private application. It is NOT affiliated with, endorsed by, or connected to any government department.",
            R.drawable.ic_warning,
            isWarning = true
        ),
        InfoItem.Section(
            "Non-Representation",
            "This application does NOT represent any government entity. We are not government officials and cannot process applications.",
            R.drawable.ic_business
        ),
        InfoItem.Section(
            "Verification Required",
            "Users should ALWAYS verify the information by visiting official government websites or contacting relevant departments directly.",
            R.drawable.ic_visibility
        ),
        InfoItem.Section(
            "No Guarantee",
            "Use of this app does not guarantee approval of any service. Approvals are subject to government authorities' rules.",
            R.drawable.ic_description
        )
    )

    private fun getSourcesItems(): List<InfoItem> = listOf(
        InfoItem.Link(
            "UIDAI (Aadhaar)",
            "Official source for Aadhaar enrollment, updates, and PVC card information.",
            "https://uidai.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "Income Tax Department",
            "Source for Income tax filing and tax-related services.",
            "https://www.incometax.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "PAN Services (Protean)",
            "Official portal for Permanent Account Number (PAN) applications.",
            "https://tinpan.proteantech.in/services/pan/pan-index.html",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "Voter ID (ECI)",
            "Election Commission of India portal for Voter registration and E-Epic.",
            "https://voters.eci.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "Passport Seva",
            "Official portal for Passport applications and appointments.",
            "https://passportindia.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "Parivahan Sewa",
            "Driving License and vehicle-related services portal.",
            "https://parivahan.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "Ayushman Bharat",
            "National Health Authority portal for PM-JAY benefits.",
            "https://beneficiary.nha.gov.in",
            R.drawable.ic_public
        ),
        InfoItem.Link(
            "DigiLocker",
            "Digital document storage and issuance platform.",
            "https://www.digilocker.gov.in",
            R.drawable.ic_public
        )
    )

    private fun getContactItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "Support Hours",
            "Mon to Sat: 10:00 AM – 7:00 PM (IST)\nSunday: Closed",
            R.drawable.ic_info
        ),
        InfoItem.Contact(
            "Support Email",
            "icc@indiacybercafe.com",
            R.drawable.ic_public,
            InfoItem.ActionType.EMAIL
        ),
        InfoItem.Contact(
            "Support Phone",
            "+91 9203251821",
            R.drawable.ic_call,
            InfoItem.ActionType.PHONE
        ),
        InfoItem.Contact(
            "Official Website",
            "https://indiacybercafe.com",
            R.drawable.ic_public,
            InfoItem.ActionType.WEB
        )
    )

    private fun getAppInfoItems(): List<InfoItem> = listOf(
        InfoItem.AppInfo("App Name", "eServices ICC Helper", R.drawable.ic_info),
        InfoItem.AppInfo("Current Version", "1.1.2", R.drawable.ic_new_releases),
        InfoItem.AppInfo("Build Number", "5", R.drawable.ic_description),
        InfoItem.AppInfo("Last Updated", "July 2026", R.drawable.ic_system_update),
        InfoItem.AppInfo("Package Name", "com.icc.eserviceshelper", R.drawable.ic_public)
    )

    private fun getDeveloperItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "About Developer",
            "Developed with a passion for digital accessibility by the technical team at India Cyber Cafe.",
            R.drawable.ic_person
        ),
        InfoItem.Contact(
            "Developer Email",
            "sanjayvaishya.dev@gmail.com",
            R.drawable.ic_public,
            InfoItem.ActionType.EMAIL
        ),
        InfoItem.Contact(
            "LinkedIn Profile",
            "https://linkedin.com/in/sanjay-vaishya-1151ba386",
            R.drawable.ic_person,
            InfoItem.ActionType.WEB
        )
    )

    private fun getOrganizationItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "India Cyber Cafe",
            "India Cyber Cafe is dedicated to making government and digital services accessible to every citizen through technology and guidance.",
            R.drawable.ic_business
        ),
        InfoItem.Contact(
            "Organization Website",
            "https://indiacybercafe.com",
            R.drawable.ic_public,
            InfoItem.ActionType.WEB
        ),
        InfoItem.Contact(
            "Contact Email",
            "support@indiacybercafe.com",
            R.drawable.ic_public,
            InfoItem.ActionType.EMAIL
        )
    )

    private fun getUpdateItems(): List<InfoItem> = listOf(
        InfoItem.Section(
            "Stay Updated",
            "Regular updates ensure you have the latest government procedures and document requirements.",
            R.drawable.ic_system_update
        ),
        InfoItem.Link(
            "Check Play Store",
            "Visit our official Play Store page to check for updates.",
            "https://play.google.com/store/apps/details?id=com.icc.eserviceshelper",
            R.drawable.ic_public
        )
    )
}