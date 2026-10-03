import { Header } from "../components/common/Header";
import { Footer } from "../components/common/Footer";

const LAST_UPDATED = "27 September 2026";
const REPO_URL = "https://github.com/Jayantaxnath/railway-eta-prediction-system";

const ExternalLink = ({ href, children }) => (
  <a href={href} target="_blank" rel="noopener noreferrer">
    {children}
  </a>
);

function PrivacyContent() {
  return (
    <>
      <section className="legal-summary" aria-label="Summary">
        <h2>Summary</h2>
        <ul>
          <li>You can use RailX without an account. We do not ask for your name, email address or phone number.</li>
          <li>RailX does not use cookies, analytics, advertising or tracking tools.</li>
          <li>Your recent searches are stored only in your browser. You can delete them at any time.</li>
          <li>Map images are loaded directly from OpenStreetMap, which receives your IP address.</li>
        </ul>
      </section>

      <h2>1. Scope</h2>
      <p>
        This Privacy Policy explains how RailX (&quot;RailX&quot;, &quot;we&quot;, &quot;us&quot;)
        handles information when you use the RailX website, including its train status, station
        board and control room pages (together, the &quot;Service&quot;).
      </p>

      <h2>2. Information processed when you use the Service</h2>
      <h3>2.1 Search queries</h3>
      <p>
        When you search for a train or a station, the text you enter (for example a train number,
        train name, station code or station name) and the journey date you select are sent to our
        server. Our server uses them to request the matching information from RailRadar (see
        section 4). Search queries are not linked to your identity.
      </p>
      <p>
        Search boxes are meant for train and station details only. Please do not enter personal
        information in them.
      </p>

      <h3>2.2 Connection information</h3>
      <p>
        As with any website, your browser sends your IP address, browser type and the address of
        the page requested when it connects to the Service. We do not use this information to
        identify you. It may be recorded in standard server logs by the infrastructure that hosts
        the Service.
      </p>

      <h2>3. Information stored on your device</h2>
      <p>
        The Service uses your browser&apos;s local storage to keep a list of your recent searches.
        Each entry contains a train number, train name, origin and destination. This list stays on
        your device and is not sent to us.
      </p>
      <p>
        You can remove a single entry with the remove button next to it, remove all entries with{" "}
        <strong>Clear all</strong> on the home page, or delete the list by clearing this
        site&apos;s data in your browser settings.
      </p>
      <p>The Service does not set cookies.</p>

      <h2>4. Third-party services</h2>
      <p>The Service relies on the following providers:</p>
      <ul>
        <li>
          <strong>RailRadar</strong> provides train positions, schedules, station boards and
          search results. It receives, from our server, the train numbers, dates, station codes and
          search text needed to answer your request. It does not receive your IP address.
        </li>
        <li>
          <strong>OpenWeatherMap</strong> provides weather conditions used in delay predictions.
          It receives, from our server, the coordinates, name or code of the station being checked.
          It does not receive any information about you.
        </li>
        <li>
          <strong>OpenStreetMap Foundation</strong> provides the map images on the train and
          control room pages. Your browser requests these images directly, so the OpenStreetMap
          Foundation receives your IP address, browser details and the map area being viewed. Its
          handling of this information is described in the{" "}
          <ExternalLink href="https://osmfoundation.org/wiki/Privacy_Policy">
            OpenStreetMap Foundation Privacy Policy
          </ExternalLink>
          .
        </li>
      </ul>

      <h2>5. Information we store</h2>
      <p>
        Our database stores information about trains: schedules, reported positions and delays,
        weather observations at stations, and arrival time predictions. It does not store
        information about the people who use the Service.
      </p>

      <h2>6. Sharing of information</h2>
      <p>
        We do not sell, rent or trade personal information, and we do not share it with
        advertisers. Information is passed to the providers in section 4 only as described there.
      </p>

      <h2>7. Children</h2>
      <p>
        The Service is not directed at children and does not knowingly collect personal information
        from anyone, including children.
      </p>

      <h2>8. Changes to this policy</h2>
      <p>
        We may update this Privacy Policy when the Service changes. The date at the top of this page
        shows when it was last updated.
      </p>

      <h2>9. Contact</h2>
      <p>
        For questions about this Privacy Policy, open an issue in the{" "}
        <ExternalLink href={`${REPO_URL}/issues`}>RailX project repository</ExternalLink>. Issues
        are publicly visible, so please do not include personal information in them.
      </p>
    </>
  );
}

function TermsContent() {
  return (
    <>
      <h2>1. Acceptance of these terms</h2>
      <p>
        These Terms &amp; Conditions (&quot;Terms&quot;) govern your use of the RailX website and
        its features (the &quot;Service&quot;). By using the Service, you agree to these Terms. If
        you do not agree, do not use the Service.
      </p>

      <h2>2. The Service</h2>
      <p>
        RailX shows train running status, station arrivals and departures, train positions on a map
        and predicted arrival times. The Service is provided free of charge and does not require an
        account. It does not sell tickets or accept payments.
      </p>

      <h2>3. Not an official service</h2>
      <p>
        RailX is an independent project. It is not operated by, affiliated with, or endorsed by
        Indian Railways, the Ministry of Railways, IRCTC, CRIS or any government body. Names of
        railways, trains and stations are used only to identify them.
      </p>

      <h2>4. Accuracy of information</h2>
      <h3>4.1 Third-party data</h3>
      <p>
        Train positions, schedules, platforms and delays are obtained from third-party sources. This
        information may be delayed, incomplete or incorrect.
      </p>
      <h3>4.2 Predictions</h3>
      <p>
        Predicted arrival times and delays are estimates produced by a statistical model. They are
        not guaranteed and can differ from actual arrival times.
      </p>
      <h3>4.3 Official sources</h3>
      <p>
        Before making travel decisions, confirm train information through official Indian Railways
        channels, such as the National Train Enquiry System (NTES), the rail enquiry number 139, or
        announcements at the station.
      </p>

      <h2>5. Acceptable use</h2>
      <p>When using the Service, you must not:</p>
      <ul>
        <li>use the Service for any unlawful purpose;</li>
        <li>send automated requests at a volume that interferes with the Service or its data providers;</li>
        <li>attempt to gain unauthorised access to the Service or the systems that run it;</li>
        <li>present information from the Service as official railway information.</li>
      </ul>

      <h2>6. Intellectual property</h2>
      <p>
        The RailX source code is available under the{" "}
        <ExternalLink href={`${REPO_URL}/blob/main/LICENSE`}>MIT License</ExternalLink>. Map data
        is &copy; OpenStreetMap contributors and is available under the Open Database License.
        Train and weather data remain the property of their respective providers and are subject to
        their terms.
      </p>

      <h2>7. Availability</h2>
      <p>
        The Service depends on third-party data providers and may be unavailable, incomplete or
        changed at any time. We may modify, suspend or discontinue any part of the Service without
        notice.
      </p>

      <h2>8. Disclaimer of warranties</h2>
      <p>
        The Service is provided &quot;as is&quot; and &quot;as available&quot;, without warranties
        of any kind, whether express or implied, including warranties of accuracy, reliability,
        fitness for a particular purpose and uninterrupted availability.
      </p>

      <h2>9. Limitation of liability</h2>
      <p>
        To the fullest extent permitted by law, RailX and its contributors are not liable for any
        loss or damage arising from your use of, or reliance on, the Service. This includes missed
        trains or connections, travel expenses and any indirect or consequential loss.
      </p>

      <h2>10. Changes to these Terms</h2>
      <p>
        We may update these Terms. The date at the top of this page shows when they were last
        updated. Continued use of the Service after an update means you accept the updated Terms.
      </p>

      <h2>11. Governing law</h2>
      <p>These Terms are governed by the laws of India.</p>

      <h2>12. Contact</h2>
      <p>
        For questions about these Terms, open an issue in the{" "}
        <ExternalLink href={`${REPO_URL}/issues`}>RailX project repository</ExternalLink>.
      </p>
    </>
  );
}

export function LegalPage({ type, onBack, onNavigateHome, onNavigateView }) {
  const isPrivacy = type === "privacy";

  return (
    <div className="result-page legal-page">
      <Header
        onNavigateHome={onNavigateHome}
        onNavigateView={onNavigateView}
        activeView={null}
      />

      <main className="legal-content">
        <button type="button" className="legal-back" onClick={onBack}>
          ← Back
        </button>

        <article className="legal-card">
          <h1>{isPrivacy ? "Privacy Policy" : "Terms & Conditions"}</h1>
          <p className="legal-updated">Last updated: {LAST_UPDATED}</p>
          {isPrivacy ? <PrivacyContent /> : <TermsContent />}
        </article>
      </main>

      <Footer />
    </div>
  );
}
