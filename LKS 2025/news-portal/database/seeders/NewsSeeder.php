<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Post;
use App\Models\Comment;
use Illuminate\Database\Seeder;

class NewsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create journalist users
        $journalists = User::factory(5)->create();

        // Create regular users for comments
        $users = User::factory(15)->create();

        // Sample news data
        $newsData = [
            [
                'title' => 'Teknologi AI Terbaru Mengubah Industri Pendidikan',
                'content' => 'Perkembangan teknologi Artificial Intelligence (AI) kini semakin pesat dan mulai merambah berbagai sektor, termasuk pendidikan. Para ahli memprediksi bahwa AI akan mengubah cara pembelajaran tradisional menjadi lebih personalized dan efektif.',
            ],
            [
                'title' => 'Ekonomi Indonesia Menunjukkan Tren Positif di Kuartal III',
                'content' => 'Berdasarkan data terbaru dari Badan Pusat Statistik, ekonomi Indonesia menunjukkan pertumbuhan yang menggembirakan di kuartal ketiga tahun ini. Sektor manufaktur dan perdagangan menjadi pendorong utama pertumbuhan tersebut.',
            ],
            [
                'title' => 'Inovasi Energi Terbarukan Solusi Masa Depan',
                'content' => 'Upaya pengembangan energi terbarukan terus digalakkan sebagai bagian dari komitmen Indonesia terhadap lingkungan. Berbagai proyek energi surya dan angin sedang dalam tahap pengembangan di berbagai daerah.',
            ],
        ];

        // Create posts with sample data
        foreach ($newsData as $news) {
            $post = Post::create([
                'title' => $news['title'],
                'news_content' => $news['content'] . ' ' . fake()->paragraphs(3, true),
                'author' => $journalists->random()->id,
            ]);

            // Create 3-8 comments per post
            Comment::factory(rand(3, 8))->create([
                'post_id' => $post->id,
                'user_id' => $users->random()->id,
            ]);
        }

        // Create additional random posts
        Post::factory(10)->create([
            'author' => $journalists->random()->id,
        ]);
    }
}
