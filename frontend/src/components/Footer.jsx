function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <section className="footer-brand-col">
          <h2 className="footer-logo">MobileZBD</h2>
          <a className="footer-store-btn" href="https://maps.google.com" target="_blank" rel="noreferrer">
            Store Location
          </a>
          <div className="footer-social-icons">
            <a href="https://facebook.com/mobilezbd" target="_blank" rel="noreferrer" aria-label="Facebook">Fb</a>
            <a href="https://instagram.com" target="_blank" rel="noreferrer" aria-label="Instagram">In</a>
            <a href="https://linkedin.com" target="_blank" rel="noreferrer" aria-label="LinkedIn">Li</a>
            <a href="https://youtube.com" target="_blank" rel="noreferrer" aria-label="Youtube">Yt</a>
          </div>
          <p>Email: admin@mobilezbd.com</p>
          <p>Phone: +880-1700-000000</p>
        </section>

        <section className="footer-col">
          <h3>Company</h3>
          <a href="#">About Us</a>
          <a href="#">Career</a>
          <a href="#">Our Brands</a>
          <a href="#">Blogs</a>
          <a href="#">Order Tracking</a>
        </section>

        <section className="footer-col">
          <h3>Help Center</h3>
          <a href="#">FAQ</a>
          <a href="#">Support Center</a>
          <a href="#">Announcement</a>
          <a href="#">Feedback</a>
          <a href="#">Sitemap</a>
        </section>

        <section className="footer-col">
          <h3>Terms & Policies</h3>
          <a href="#">Terms & Conditions</a>
          <a href="#">Refund Policy</a>
          <a href="#">Privacy Policy</a>
          <a href="#">Warranty Policy</a>
          <a href="#">Exchange Policy</a>
        </section>

        <section className="footer-col footer-branches">
          <h3>Stay Connected</h3>
          <p>Branch 1: Jamuna Future Park, Dhaka</p>
          <p>Branch 2: Bashundhara City, Dhaka</p>
          <p>Branch 3: GEC Circle, Chittagong</p>
          <p>Branch 4: Uttara, Dhaka</p>
        </section>
      </div>
      <div className="footer-bottom">
        <small>© 2026 MobileZBD Ltd. | All rights reserved</small>
      </div>
    </footer>
  );
}

export default Footer;
